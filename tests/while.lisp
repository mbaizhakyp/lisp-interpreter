(set i 0)
(set sum 0)
(while (less i 5) (set sum (add sum i)) (set i (add i 1))) ; body may hold several expressions
sum
i
(while () (set i 100)) ; a false test never runs the body
i
