; uses all the new features together
(def fizzbuzz (n)
  (let ((i 1))
    (while (less i (add n 1))
      (print (cond ((eq? (rem i 15) 0) "FizzBuzz"
                    (eq? (rem i 3) 0) "Fizz"
                    (eq? (rem i 5) 0) "Buzz"
                    T i)))
      (set i (add i 1)))
    "done"))
(fizzbuzz 15)
(set squares (lambda (n) (let ((i 1) (out ())) (while (less i (add n 1)) (set out (cons (mul i i) out)) (set i (add i 1))) out)))
(squares 5)
