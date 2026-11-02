(lambda (x) (mul x x)) ; lambda builds a function expression
((lambda (x) (mul x x)) 5) ; and it can be called directly
((function (a b) (sub a b)) 10 4)
(set twice (lambda (f x) (f (f x))))
(def inc (n) (add n 1))
(twice inc 5)
((a b) c) ; a list that starts with a non-function still evaluates to itself
