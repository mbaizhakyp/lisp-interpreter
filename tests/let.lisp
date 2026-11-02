(let ((x 2) (y 3)) (mul x y))
x ; x is not set outside the let
(set x 10)
(let ((x 1) (y x)) (add x y)) ; values are evaluated outside the let, so y is 10
(def hyp2 (a b) (let ((aa (mul a a)) (bb (mul b b))) (add aa bb))) ; a let can see the function's parameters
(hyp2 3 4)
