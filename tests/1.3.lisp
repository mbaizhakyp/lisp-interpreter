(set a 2) ; set returns the value
a ; implied lookup
x ; never set, so it evaluates to itself
(set a 3)
a ; the newest definition wins
(nil? ())
(nil? 'T)
(atom? x)
(atom? (x))
(list? 'T)
(list? x)
(list? (x))
