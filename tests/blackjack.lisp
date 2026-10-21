; Blackjack. Cards are 2 to 10 (face cards are 10) and 1 for an ace.
; An ace counts as 11 when that does not bust the hand.
(def sum (hand) (if (nil? hand) 0 (add (car hand) (sum (cdr hand)))))
(def has-ace? (hand) (cond (
	(nil? hand) ()
	(eq? (car hand) 1) 'T
	'T (has-ace? (cdr hand)))))
(def score (hand) (if (and? (has-ace? hand) (less (sum hand) 12)) (add (sum hand) 10) (sum hand)))
(def bust? (hand) (less 21 (score hand)))
; the dealer draws from the deck while under 17, and returns the final hand
(def dealer (hand deck) (if (less (score hand) 17) (dealer (cons (car deck) hand) (cdr deck)) hand))
(def winner (player house) (cond (
	(bust? player) 'dealer
	(bust? house) 'player
	(less (score house) (score player)) 'player
	(less (score player) (score house)) 'dealer
	'T 'push)))
(def play (player house deck) (winner player (dealer house deck)))
(score '(1 10)) ; blackjack
(score '(1 1 9)) ; one ace counts as 11
(score '(10 5 1)) ; the ace must count as 1
(bust? '(10 5 9))
(dealer '(10 4) '(2 5 9)) ; draws to 16, then 21
(play '(10 9) '(10 6) '(5 3)) ; dealer draws to 21
(play '(10 9) '(10 7) '(5)) ; dealer stands on 17
(play '(10 8) '(10 6) '(10)) ; dealer busts
(play '(10 10 5) '(10 6) '(2)) ; player busts
(play '(10 8) '(10 8) ()) ; tie
