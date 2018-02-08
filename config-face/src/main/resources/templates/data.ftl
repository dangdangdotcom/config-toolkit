[#ftl]
<div class="input-group d-flex justify-content-end mb-2">
    <div class="input-group-prepend">
        <input type="text" name="key" required spellcheck="false" class="form-control" style="font-size: small;" placeholder="Key" aria-label="Key" aria-describedby="basic-addon2">
        <input type="text" name="value" spellcheck="false" class="form-control" style="font-size: small;" placeholder="Value" aria-label="Value" aria-describedby="basic-addon2">
        <input type="text" name="comment" spellcheck="false" class="form-control" style="font-size: small;" placeholder="Comment" aria-label="Comment" aria-describedby="basic-addon2">

        <input type="hidden" name="version" value="${version}">
        <input type="hidden" name="group" value="${group}">
    </div>
    <button name="newProp" class="btn btn-outline-secondary" style="font-size: small;" type="button">Add</button>
</div>
<div class="table-responsive datas">
    <table class="table table-sm">
        <thead>
        <tr>
            <th>#</th>
            <th>Key</th>
            <th>Value</th>
            <th>Comment</th>
        </tr>
        </thead>
        <tbody>
        [#if items??]
            [#list items as item]
                <tr>
                    <td>
                        <div style="width: 4em;">
                            <a version="${version}" group="${group}" updateprop="${item.name}" href="#"><img src="/image/pencil.png"></a>
                            <a version="${version}" group="${group}" delprop="${item.name}" href="#"><img src="/image/trash.png"></a>
                        </div>
                    </td>
                    <td name="name">${item.name}</td>
                    <td name="value">${item.value}</td>
                    <td name="comment">${item.comment!""}</td>
                </tr>
            [/#list]
        [/#if]
        </tbody>
    </table>
</div>