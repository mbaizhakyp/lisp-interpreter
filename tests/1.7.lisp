(set plus (function (a b) (add a b)))
(plus 11 12)
(set a 100)
(plus a 1) ; arguments are evaluated in the caller's environment
a ; the frame is popped after the call
(set fact (function (n) (if (less n 2) 1 (mul n (fact (sub n 1))))))
(fact 5) ; recursion
(set len (function (l) (if (nil? l) 0 (add 1 (len (cdr l))))))
(len '(a b c d))
