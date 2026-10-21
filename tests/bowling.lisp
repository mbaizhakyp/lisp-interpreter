; Bowling score, from the instructor's program. r is the list of rolls.
; Fixes: frame's cond was missing its open-frame case and 3 closing parens.
(def sum2 (r) (add (car r) (car (cdr r))))
(def sum3 (r) (add (sum2 r) (car (cdr (cdr r)))))
(def strike? (r) (nil? (less (car r) 10)))
(def spare? (r) (nil? (less (sum2 r) 10)))
(def frame (r n)
 (cond ((less 10 n) 0
     (strike? r) (add (sum3 r) (frame (cdr r) (add n 1)))
     (spare? r) (add (sum3 r) (frame (cdr (cdr r)) (add n 1)))
     'T (add (sum2 r) (frame (cdr (cdr r)) (add n 1))))))
(def bowling (r) (frame r 1))
(bowling '(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)) ; gutter game
(bowling '(3 4 3 4 3 4 3 4 3 4 3 4 3 4 3 4 3 4 3 4)) ; all open frames
(bowling '(9 1 9 1 9 1 9 1 9 1 9 1 9 1 9 1 9 1 9 1 9)) ; all spares
(bowling '(10 10 10 10 10 10 10 10 10 10 10 10)) ; perfect game
(bowling '(10 7 3 9 0 10 0 8 8 2 0 6 10 10 10 8 1)) ; mixed
