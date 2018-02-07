[#ftl]
<div class="table-responsive">
    <table class="table table-sm" style="font-size: small;">
        <thead>
        <tr>
            <th scope="col" colspan="4">
                <div class="input-group d-flex justify-content-end">
                    <div class="input-group-prepend">
                        <input type="text" name="key" required spellcheck="false" class="form-control" style="font-size: small;" placeholder="Key" aria-label="Key" aria-describedby="basic-addon2">
                        <input type="text" name="value" spellcheck="false" class="form-control" style="font-size: small;" placeholder="Value" aria-label="Value" aria-describedby="basic-addon2">
                        <input type="text" name="comment" spellcheck="false" class="form-control" style="font-size: small;" placeholder="Comment" aria-label="Comment" aria-describedby="basic-addon2">

                        <input type="hidden" name="version" value="${version}">
                        <input type="hidden" name="group" value="${group}">
                    </div>
                    <button name="newProp" class="btn btn-outline-secondary" style="font-size: small;" type="button">Add</button>
                </div>
            </th>
        </tr>
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