package com.example.foodaap.Module

class Sorter {

        companion object {
            var costComparator = Comparator<Restaurant> { res1, res2 ->
                val costOne = res1.cost_for_one as Int
                val costTwo = res2.cost_for_one as Int
                if (costOne.compareTo(costTwo) == 0) {
                    ratingComparator.compare(res1, res2)
                } else {
                    costOne.compareTo(costTwo)
                }
            }

            var ratingComparator = Comparator<Restaurant> { res1, res2 ->
                val ratingOne = res1.rating as String
                val ratingTwo = res2.rating as String
                if (ratingOne.compareTo(ratingTwo) == 0) {
                    val costOne = res1.cost_for_one as Int
                    val costTwo = res2.cost_for_one as Int
                    costOne.compareTo(costTwo)
                } else {
                    ratingOne.compareTo(ratingTwo)
                }
            }
        }

    }





