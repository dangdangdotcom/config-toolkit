IndexPage = {
    init: function () {
        IndexPage.bindingEvents();
    },
    bindingEvents: function () {
        var dataGroups = $("#groupList").find('a[data-group]');
        dataGroups.bind('click', function(e) {
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
    }
}
IndexPage.init();