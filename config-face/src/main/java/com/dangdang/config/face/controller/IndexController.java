package com.dangdang.config.face.controller;

import com.dangdang.config.face.dao.NodeService;
import com.dangdang.config.face.entity.CommonResponse;
import com.dangdang.config.face.entity.PropertyItem;
import com.dangdang.config.face.entity.PropertyItemVO;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class IndexController {

    @Autowired
    private NodeService nodeService;

    private static final String COMMENT_SUFFIX = "$";

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(value = {"", "/config-web"}, method = RequestMethod.GET)
    public String index() {
        return "redirect:/c/" + encodeRootNode(getRoot());
    }

    private String encodeRootNode(String root) {
        return StringUtils.replace(root, "/", "_");
    }

    private String decodeRootNode(String root) {
        return StringUtils.replace(root, "_", "/");
    }

    @RequestMapping(value = {"/c/{rootNode}", "/c/{rootNode}/{version:.+}"}, method = RequestMethod.GET)
    public ModelAndView rootNode(@PathVariable String rootNode, @PathVariable(required = false) String version) {
        final String theRoot = decodeRootNode(rootNode);
        final String root = getRoot();

        if (!Objects.equals(theRoot, root)) {
            return new ModelAndView("redirect:/c/" + encodeRootNode(root));
        }

        final List<String> versions = nodeService.listChildren(theRoot)
                .stream().filter(e -> !e.endsWith(COMMENT_SUFFIX))
                .sorted(Comparator.comparing(String::toString).reversed())
                .collect(Collectors.toList());

        final String theVersion = com.google.common.base.Objects.firstNonNull(version, Iterables.getFirst(versions, null));

        final ModelAndView mv = new ModelAndView("index");
        mv.addObject("root", theRoot);
        mv.addObject("versions", versions);
        mv.addObject("theVersion", theVersion);
        mv.addObject("basePath", "/c/" + rootNode + "/");

        if (Iterables.contains(versions, theVersion)) {
            final List<String> groups = nodeService.listChildren(makePaths(theRoot, theVersion))
                    .stream().sorted().collect(Collectors.toList());
            mv.addObject("groups", groups);
        }

        return mv;
    }

    @RequestMapping(value = "/group/{version}/{group}", method = RequestMethod.GET)
    public ModelAndView groupData(@PathVariable String version, @PathVariable String group) {
        final String root = getRoot();

        List<PropertyItemVO> items = Lists.newArrayList();

        final List<PropertyItem> props = nodeService.findProperties(makePaths(root, version, group));
        final List<PropertyItem> itemComment = nodeService.findProperties(makePaths(root, version + COMMENT_SUFFIX, group));
        if (props != null) {
            Map<String, String> comments = Maps.newHashMap();
            if (itemComment != null) {
                for (PropertyItem comment : itemComment) {
                    comments.put(comment.getName(), comment.getValue());
                }
            }

            for (PropertyItem propertyItem : props) {
                PropertyItemVO vo = new PropertyItemVO(propertyItem);
                vo.setComment(comments.get(propertyItem.getName()));
                items.add(vo);
            }

            Collections.sort(items);
        }

        final ModelAndView mv = new ModelAndView("data", "items", items);
        mv.addObject("version", version);
        mv.addObject("group", group);

        return mv;
    }

    @RequestMapping(value = "/group/{version:.+}", method = RequestMethod.POST)
    public ModelAndView createGroup(@PathVariable String version, String newGroup) {
        final String root = getRoot();

        final String groupPath = makePaths(root, version, newGroup);

        nodeService.createProperty(groupPath);

        return new ModelAndView("redirect:/c/" + encodeRootNode(root) + "/" + version);
    }

    @RequestMapping(value = "/version/{version:.+}", method = RequestMethod.POST)
    public @ResponseBody
    CommonResponse<Object> createVersion(@PathVariable String version, String fromVersion) {
        LOGGER.debug("Create version {} from {}", version, fromVersion);

        version = StringUtils.trim(version);
        fromVersion = StringUtils.trim(fromVersion);

        if (!Strings.isNullOrEmpty(version)) {
            final String root = getRoot();

            final String versionNode = makePaths(root, version);
            boolean suc = nodeService.createProperty(versionNode);

            if (suc) {
                nodeService.createProperty(versionNode + COMMENT_SUFFIX);
                if (!Strings.isNullOrEmpty(fromVersion)) {
                    final String fromVersionNode = makePaths(root, fromVersion);
                    cloneVersion(fromVersionNode, versionNode);
                    cloneVersion(fromVersionNode + COMMENT_SUFFIX, versionNode + COMMENT_SUFFIX);
                }

                return new CommonResponse<>(true, "/c/" + encodeRootNode(root) + "/" + version, null);
            }
        }

        return new CommonResponse<>(false, null, "Invalid Args");
    }

    private void cloneVersion(String sourceVersionPath, String destinationVersionPath) {
        List<String> sourceGroups = nodeService.listChildren(sourceVersionPath);
        if (sourceGroups != null) {
            for (String sourceGroup : sourceGroups) {
                String sourceGroupFullPath = makePaths(sourceVersionPath, sourceGroup);
                String destinationGroupFullPath = makePaths(destinationVersionPath, sourceGroup);

                nodeService.createProperty(destinationGroupFullPath, null);
                List<PropertyItem> sourceProperties = nodeService.findProperties(sourceGroupFullPath);
                if (sourceProperties != null) {
                    for (PropertyItem sourceProperty : sourceProperties) {
                        nodeService.createProperty(makePaths(destinationGroupFullPath, sourceProperty.getName()), sourceProperty.getValue());
                    }
                }
            }
        }
    }

    @RequestMapping(value = "/prop", method = RequestMethod.POST)
    public @ResponseBody
    CommonResponse<Object> createProp(String version, String group, String key, String value, String comment) {
        final String root = getRoot();

        final String propPath = makePaths(root, version, group, key);
        final boolean suc = nodeService.createProperty(propPath, value);

        if (suc) {
            if (!Strings.isNullOrEmpty(comment)) {
                final String commentPath = makePaths(root, version + COMMENT_SUFFIX, group, key);
                nodeService.createProperty(commentPath, comment);
            }

            return new CommonResponse<>(true, null, null);
        }

        return new CommonResponse<>(false, null, "Server Error");

    }

    @RequestMapping(value = "/prop", method = RequestMethod.PUT)
    public @ResponseBody
    CommonResponse<Object> updateProp(String version, String group, String key, String value, String comment) {
        final String root = getRoot();

        final String propPath = makePaths(root, version, group, key);
        final String commentPath = makePaths(root, version + COMMENT_SUFFIX, group, key);

        nodeService.updateProperty(propPath, value);
        nodeService.updateProperty(commentPath, comment);

        return new CommonResponse<>(true, null, null);


    }

    @RequestMapping(value = "/prop/{version}/{group}/{key}", method = RequestMethod.DELETE)
    public @ResponseBody
    CommonResponse<Object> deleteProp(@PathVariable String version, @PathVariable String group, @PathVariable String key) {
        final String root = getRoot();

        final String propPath = makePaths(root, version, group, key);

        nodeService.deleteProperty(propPath);

        return new CommonResponse<>(true, null, null);

    }

    private String getRoot() {
        final UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }

    @RequestMapping(value = "/group/{version}/{group}", method = RequestMethod.DELETE)
    public @ResponseBody
    CommonResponse<Object> deleteGroup(@PathVariable String version, @PathVariable String group) {
        final String root = getRoot();
        final String versionPath = makePaths(root, version, group);

        nodeService.deleteProperty(versionPath);

        return new CommonResponse<>(true, null, null);
    }

    private String makePaths(String root, String first, String... others) {
        String path = ZKPaths.makePath(root, first);
        if (others != null) {
            for (String other : others) {
                path = ZKPaths.makePath(path, other);
            }
        }
        return path;
    }

}
