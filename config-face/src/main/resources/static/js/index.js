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
                    $("#dataD").find("[name=key]:first").focus();
                }
            });
        });

        $("#newVersionButton").bind("click", function (e) {
            e.preventDefault();
            var newVersionForm = $(this).parents("form");
            var version = newVersionForm.find("input[name=version]:first").val();
            var fromVersion = newVersionForm.find("select[name=fromVersion]:first").val();
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

        $("#dataD").on("click", "button[name=newProp]", function (e) {
            e.preventDefault();
            var inputGroup = $(this).parents(".input-group");
            var version = inputGroup.find("[name=version]:first").val();
            var group = inputGroup.find("[name=group]:first").val();
            var key = inputGroup.find("[name=key]:first").val();
            var value = inputGroup.find("[name=value]:first").val();
            var comment = inputGroup.find("[name=comment]:first").val();
            
            $.ajax({
                url: "/prop",
                method: "post",
                data: {
                    "version": version,
                    "group": group,
                    "key": key,
                    "value": value,
                    "comment": comment
                },
                success: function (data) {
                    if(data.suc) {
                        alert("Success");
                        $("#groupList").find("a[data-group].active").click();
                    } else {
                        alert(data.message);
                    }
                }
            });
        });
    }
}
IndexPage.init();