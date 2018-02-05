[#ftl]
<div class="table-responsive">
    <table class="table table-sm" style="font-size: small;">
        <thead>
        <tr>
            <th scope="col">#</th>
            <th scope="col">Key</th>
            <th scope="col">Value</th>
            <th scope="col">Comment</th>
        </tr>
        </thead>
        <tbody>
        [#if items??]
            [#list items as item]
                <tr>
                    <td></td>
                    <td>${item.name}</td>
                    <td>${item.value}</td>
                    <td>${item.comment!""}</td>
                </tr>
            [/#list]
        [/#if]
        </tbody>
    </table>
</div>