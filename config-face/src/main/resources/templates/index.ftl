[#ftl]
[#import 'common.ftl' as c]
<html lang="en">
<head>
    [@c.head/]

    <link href="/css/index.css" rel="stylesheet">
    <script src="/js/index.js" type="text/javascript" defer></script>
    <title>Index</title>
</head>

<body>
    <nav class="navbar navbar-dark bg-dark navbar-expand-lg justify-content-between fixed-top">
        <span class="navbar-brand">${root}</span>

        <ul class="navbar-nav" style="min-width: 4.5em;">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="versionDD" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    ${theVersion!"Version"}
                </a>
                <div class="dropdown-menu" aria-labelledby="versionDD">
                [#if versions??]
                    [#list versions as version]
                        <a class="dropdown-item" href="/version/${version}">${version}</a>
                    [/#list]
                [/#if]
                </div>
            </li>
        </ul>

        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <button class="btn btn-sm btn-outline-secondary mr-auto mybtn" type="button" data-toggle="modal" data-target="#newModal">New</button>
            <button class="btn btn-sm btn-outline-secondary mybtn" [#if theVersion??][#else]disabled[/#if] type="button" data-toggle="modal" data-target="#importModal">Import</button>
            <button class="btn btn-sm btn-outline-secondary mybtn" [#if theVersion??][#else]disabled[/#if] type="button" data-toggle="modal" data-target="#exportModal">Export</button>
            <a href="/logout"><img class="ml-4" style="margin-top: 0.4em;" src="/image/account-logout.svg"></a>
        </div>
    </nav>

    <div class="container-fluid">
        <div class="row">
            <div class="col-3" style="font-size: smaller;">
                <div class="groups">
                    <ul class="list-group" id="groupList">
                        [#if groups??]
                            [#list groups as group]
                                <li data-group="${group}" class="list-group-item d-flex justify-content-between align-items-center">
                                    ${group}
                                        <a href="#" version="${theVersion}" group="${group}" style="display: none"><img src="/image/trash.png"></a>
                                </li>
                            [/#list]
                        [/#if]
                    </ul>
                </div>

                <form action="/group/${theVersion!""}" method="post">
                    <div class="input-group mt-2" style="margin-top: 1em;">
                        <input type="text" required spellcheck="false" name="newGroup" class="form-control" style="font-size: small;" placeholder="Group Name" aria-label="group name" aria-describedby="basic-addon2">
                        <div class="input-group-append">
                            <button class="btn btn-outline-secondary" style="font-size: small;" [#if theVersion??][#else]disabled[/#if] type="submit">Add</button>
                        </div>
                    </div>
                </form>
            </div>

            <div class="col-9" style="font-size: small;" id="dataD">
                <div class="text-center mt-5" style="opacity: 0.3;"><h2>Choose A Group</h2></div>
            </div>
        </div>
    </div>

    <!-- New version Modal -->
    <div class="modal fade" id="newModal" tabindex="-1" role="dialog" aria-labelledby="newModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document" style="width: 29em;">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">Create Version</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form method="post" action="/version">
                        <div class="form-row align-items-center">
                            <div class="col-auto">
                                <label class="sr-only" for="versionInput">Version</label>
                                <input type="text" required name="version" class="form-control" style="width:8em;" id="versionInput" placeholder="Version">
                            </div>
                            <div class="col-auto">
                                <label class="sr-only" for="inlineFormInputGroup">Clone</label>
                                <div class="input-group">
                                    <div class="input-group-prepend">
                                        <div class="input-group-text">Clone from</div>
                                    </div>
                                    <select class="form-control custom-select mr-sm-2" name="fromVersion" id="inlineFormInputGroup">
                                        <option value="" selected>None</option>
                                        [#if versions??]
                                            [#list versions as version]
                                                <option value="${version}">${version}</option>
                                            [/#list]
                                        [/#if]
                                    </select>
                                </div>
                            </div>
                            <div class="col-auto">
                                <button type="submit" id="newVersionButton" class="btn btn-outline-dark">Submit</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Update Property Modal -->
    <div class="modal fade" id="updatePropModal" tabindex="-1" role="dialog" aria-labelledby="updatePropLabel" data-backdrop="false" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Update Property</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="form-group row">
                        <label for="updateKey" class="col-sm-2 col-form-label">Key</label>
                        <div class="col-sm-10">
                            <input type="text" readonly class="form-control-plaintext" id="updateKey">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="updateValue" class="col-sm-2 col-form-label">Value</label>
                        <div class="col-sm-10">
                            <input type="text" required class="form-control" id="updateValue">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="updateComment" class="col-sm-2 col-form-label">Comment</label>
                        <div class="col-sm-10">
                            <input type="text" required class="form-control" id="updateComment">
                        </div>
                    </div>
                    <input type="hidden" name="updateVersion" id="updateVersion">
                    <input type="hidden" name="updateGroup" id="updateGroup">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-dark" name="updateButton">Save changes</button>
                    <button type="button" class="btn btn-outline-dark" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Export Modal -->
    <div class="modal fade" id="exportModal" tabindex="-1" role="dialog" aria-labelledby="exportLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Choose</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <a href="/export/${theVersion!""}" class="btn btn-outline-dark btn-lg btn-block" id="exportVersionBt">Export Version</a>
                    <a href="#" class="btn btn-outline-dark btn-lg btn-block disabled" id="exportGroupBt">Export Group</a>
                </div>
            </div>
        </div>
    </div>

    <!-- Import Modal -->
    <div class="modal fade" id="importModal" tabindex="-1" role="dialog" aria-labelledby="importLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Select properties or zip file</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form action="/import/${theVersion!""}" method="post" enctype="multipart/form-data">
                        <div class="input-group mb-3">
                            <div class="custom-file">
                                <input type="file" name="file" class="custom-file-input" id="importFile">
                                <label class="custom-file-label" for="importFile">Choose file</label>
                            </div>
                            <div class="input-group-append">
                                <button type="submit" class="btn btn-outline-secondary">Upload</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>