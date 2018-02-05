package com.dangdang.config.face.controller;

import com.dangdang.config.face.dao.NodeDao;
import com.dangdang.config.face.entity.PropertyItem;
import com.dangdang.config.face.entity.PropertyItemVO;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.utils.ZKPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class IndexController {

    @Autowired
    private NodeDao nodeDao;

    private static final String COMMENT_SUFFIX = "$";

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        final UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "redirect:/c/" + StringUtils.replace(principal.getUsername(), "/", "_");
    }

    @RequestMapping(value = {"/c/{rootNode}", "/c/{rootNode}/{version:.+}"})
    public ModelAndView rootNode(@PathVariable String rootNode, @PathVariable(required = false) String version) {
        final String root = StringUtils.replace(rootNode, "_", "/");
        final UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!Objects.equals(root, principal.getUsername())) {
            throw new AccessDeniedException("NOT YOUR NODE");
        }

        final List<String> versions = nodeDao.listChildren(root)
                .stream().filter(e -> ! e.endsWith(COMMENT_SUFFIX)).collect(Collectors.toList());

        final ModelAndView mv = new ModelAndView("index");
        mv.addObject("root", root);
        mv.addObject("versions", versions);
        mv.addObject("theVersion", version);
        mv.addObject("basePath", "/c/" + rootNode + "/");

        if(Iterables.contains(versions, version)) {
            final List<String> groups = nodeDao.listChildren(ZKPaths.makePath(root, version));
            mv.addObject("groups", groups);
        }

        return mv;
    }

    @RequestMapping(value = "/group/{version}/{group}")
    public ModelAndView getData(@PathVariable String version, @PathVariable String group) {
        final UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final String root = principal.getUsername();

        List<PropertyItemVO> items = Lists.newArrayList();

        final List<PropertyItem> props = nodeDao.findProperties(ZKPaths.makePath(root, version, group));
        final List<PropertyItem> itemComment = nodeDao.findProperties(ZKPaths.makePath(root, version + COMMENT_SUFFIX, group));
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

        return new ModelAndView("data", "items", items);
    }

}
