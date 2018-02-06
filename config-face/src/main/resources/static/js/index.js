IndexPage = {
    init: function () {
        IndexPage.bindingEvents();
    },
    bindingEvents: function () {
        var dataGroups = $("#groupList").find("a[data-group]");
        dataGroups.bind("click", function(e) {
            e.preventDefault();
            var groupLink = $(this);
            dataGroups.removeClass("active");
            groupLink.addClass("active");
            var version = $("#versionDD").text().trim();
            var group = groupLink.attr("data-group");
            $.ajax({
                url: "/group/" + version + "/" + group,
                method: "get",
                success: function (data) {
                    $("#dataD").html(data);
                }
            });
        });

        $("#newVersionButton").bind("click", function (e) {
            e.preventDefault();
            var newVersionForm = $(this).parents("form");
            var version = newVersionForm.find("input[name=version]").val();
            var fromVersion = newVersionForm.find("select[name=fromVersion]").val();
            $.ajax({
                url: "/version",
                method: "post",
                data: {
                    "version": version,
                    "fromVersion": fromVersion
                },
                success: function (data) {
                    if(data.suc) {
                        location.href = data.body;
                    }else {
                        alert(data.message);
                    }
                }
            });
        });
    }
}
IndexPage.init();