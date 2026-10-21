(function (a b) (add a b)) ; a function expression evaluates to itself
(set foo (function (a b) (add a b)))
foo
(set bar (function () x)) ; empty argument list
