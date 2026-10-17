(def sum2 (r) (add (car r) (car (cdr r))))


(def sum3 (r) (add (sum2 r) (car (cdr (cdr r)))))


(def strike? (r) (nil? (less (car r) 10)))


(def spare? (r) (nil? (less (sum2 r) 10)))


(def frame (r n)

 (cond ((less 10 n) 0

     (strike? r) (add (sum3 r) (frame (cdr r) (add n 1)))

     (spare? r) (add (sum3 r) (frame (cdr (cdr r)) (add n 1)))


(def bowling (r) (frame r 1))
