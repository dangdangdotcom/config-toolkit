[#ftl]
[#import 'common.ftl' as c]
<html lang="en">

<head>
    [@c.head/]
    <link href="/css/floating-labels.css" rel="stylesheet">

    <title>Login</title>
</head>

<body>
<form class="form-signin" method="post">
    <div class="text-center mb-4">
        <img class="navbar-brand" src="/image/c.png" style="width:3em;">
    </div>

    <div class="form-label-group">
        <input type="text" id="username" name="username" class="form-control" placeholder="Root Node" required="" autofocus="">
        <label for="username">Root Node</label>
    </div>

    <div class="form-label-group">
        <input type="password" id="password" name="password" class="form-control" placeholder="Password" required="">
        <label for="password">Password</label>
    </div>
    <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
</form>

</body>
</html>