(print 'a '(b c) (add 1 2)) ; prints its values on one line, returns the last one
(print)
(def show (n) (if (less 0 n) (begin (print n) (show (sub n 1))) 'done))
(show 3)
