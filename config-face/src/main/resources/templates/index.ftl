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

        <ul class="navbar-nav">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="versionDD" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    ${theVersion!"Version"}
                </a>
                <div class="dropdown-menu" aria-labelledby="versionDD">
                [#if versions??]
                    [#list versions as version]
                        <a class="dropdown-item" href="${basePath}${version}">${version}</a>
                    [/#list]
                [/#if]
                </div>
            </li>
        </ul>

        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <button class="btn btn-sm btn-outline-secondary mr-auto mybtn" type="button">Create</button>
            <button class="btn btn-sm btn-outline-secondary mybtn" type="button">Import</button>
            <button class="btn btn-sm btn-outline-secondary mybtn" type="button">Export</button>
        </div>
    </nav>

    <div class="container-fluid" style="margin-top: 4.2em;">
        <div class="row">
            <div class="col-3" style="margin: 0.5em 0 0.5em 0; font-size: smaller;">
                <div class="list-group" id="groupList">
                    [#if groups??]
                        [#list groups as group]
                            <a href="#" data-group="${group}" class="list-group-item list-group-item-action">${group}</a>
                        [/#list]
                    [/#if]
                </div>
            </div>

            <div class="col-9" style="margin: 0.5em 0 0.5em 0; font-size: small;" id="dataD"></div>
        </div>
    </div>

</body>
</html>