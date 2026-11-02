(def sumto (n) (let ((i 0) (s 0)) (while (less i n) (set i (add i 1)) (set s (add s i))) s))
(sumto 10)
i ; set changed the let's local i, not a global
(def countdown (n) (begin (while (less 0 n) (print n) (set n (sub n 1))) n))
(countdown 3)
n
