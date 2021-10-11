package com.example.restaurantfinder.ui

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.restaurantfinder.R
import com.example.restaurantfinder.databinding.CustomRatingRowBinding

/**
 * custom view to handle the rating view and user rating count
 */
class RatingRow @JvmOverloads constructor(private val ctx: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ConstraintLayout(ctx, attrs, defStyle) {

    private var binding: CustomRatingRowBinding

    init {
        inflate(ctx, R.layout.custom_rating_row, this)
        binding = CustomRatingRowBinding.bind(this)
        if (!isInEditMode) {
            setupView(attrs)
        }
    }

    private fun setupView(attrs: AttributeSet?) {
        attrs?.let { att -> context.obtainStyledAttributes(att, R.styleable.RatingRwoStyle) }
            ?.let { typedArray ->
                setRating(
                    typedArray.getFloat(R.styleable.RatingRwoStyle_rating, 0f),
                    typedArray.getInt(R.styleable.RatingRwoStyle_ratingCount, 0)
                )
            }

    }

    fun setRating(rating: Float = 0.0f, ratingCount: Int = 0) {
        binding.tvRating.text = rating.toString()
        binding.tvRatingCount.text = context.getString(R.string.rating_count, ratingCount)
        when {
            rating >= 5f -> {
                // 5
                with(binding) {
                    ivStarOne.setImageResource(R.drawable.ic_start_full)
                    ivStarTwo.setImageResource(R.drawable.ic_start_full)
                    ivStarThree.setImageResource(R.drawable.ic_start_full)
                    ivStarFour.setImageResource(R.drawable.ic_start_full)
                    ivStarFive.setImageResource(R.drawable.ic_start_full)
                }
            }
            rating > 4.8f -> {
                // 4.5
                with(binding) {
                    ivStarOne.setImageResource(R.drawable.ic_start_full)
                    ivStarTwo.setImageResource(R.drawable.ic_start_full)
                    ivStarThree.setImageResource(R.drawable.ic_start_full)
                    ivStarFour.setImageResource(R.drawable.ic_start_full)
                    ivStarFive.setImageResource(R.drawable.ic_star_half)
                }
            }
            rating > 4f -> {
                // 4
                with(binding) {
                    ivStarOne.setImageResource(R.drawable.ic_start_full)
                    ivStarTwo.setImageResource(R.drawable.ic_start_full)
                    ivStarThree.setImageResource(R.drawable.ic_start_full)
                    ivStarFour.setImageResource(R.drawable.ic_start_full)
                    ivStarFive.setImageResource(R.drawable.ic_star_empty)
                }
            }
            rating > 3.8f -> {
                // 3.5
                with(binding) {
                    ivStarOne.setImageResource(R.drawable.ic_start_full)
                    ivStarTwo.setImageResource(R.drawable.ic_start_full)
                    ivStarThree.setImageResource(R.drawable.ic_start_full)
                    ivStarFour.setImageResource(R.drawable.ic_star_half)
                    ivStarFive.setImageResource(R.drawable.ic_star_empty)
                }
            }
            rating > 3f -> {
                // 3
                with(binding) {
                    ivStarOne.setImageResource(R.drawable.ic_start_full)
                    ivStarTwo.setImageResource(R.drawable.ic_start_full)
                    ivStarThree.setImageResource(R.drawable.ic_start_full)
                    ivStarFour.setImageResource(R.drawable.ic_star_empty)
                    ivStarFive.setImageResource(R.drawable.ic_star_empty)
                }
            }
            rating > 2.8f -> {
                // 2.5
                with(binding) {
                    ivStarOne.setImageResource(R.drawable.ic_start_full)
                    ivStarTwo.setImageResource(R.drawable.ic_start_full)
                    ivStarThree.setImageResource(R.drawable.ic_star_half)
                    ivStarFour.setImageResource(R.drawable.ic_star_empty)
                    ivStarFive.setImageResource(R.drawable.ic_star_empty)
                }
            }
            rating > 2f -> {
                // 2
                with(binding) {
                    ivStarOne.setImageResource(R.drawable.ic_start_full)
                    ivStarTwo.setImageResource(R.drawable.ic_start_full)
                    ivStarThree.setImageResource(R.drawable.ic_star_empty)
                    ivStarFour.setImageResource(R.drawable.ic_star_empty)
                    ivStarFive.setImageResource(R.drawable.ic_star_empty)
                }
            }
            rating > 1.8f -> {
                // 1.5
                with(binding) {
                    ivStarOne.setImageResource(R.drawable.ic_start_full)
                    ivStarTwo.setImageResource(R.drawable.ic_star_half)
                    ivStarThree.setImageResource(R.drawable.ic_star_empty)
                    ivStarFour.setImageResource(R.drawable.ic_star_empty)
                    ivStarFive.setImageResource(R.drawable.ic_star_empty)
                }
            }
            rating == 1f -> {
                // 1 star
                with(binding) {
                    ivStarOne.setImageResource(R.drawable.ic_start_full)
                    ivStarTwo.setImageResource(R.drawable.ic_star_empty)
                    ivStarThree.setImageResource(R.drawable.ic_star_empty)
                    ivStarFour.setImageResource(R.drawable.ic_star_empty)
                    ivStarFive.setImageResource(R.drawable.ic_star_empty)
                }
            }
            rating > 0.8f -> {
                // half star
                with(binding) {
                    ivStarOne.setImageResource(R.drawable.ic_star_half)
                    ivStarTwo.setImageResource(R.drawable.ic_star_empty)
                    ivStarThree.setImageResource(R.drawable.ic_star_empty)
                    ivStarFour.setImageResource(R.drawable.ic_star_empty)
                    ivStarFive.setImageResource(R.drawable.ic_star_empty)
                }
            }
            else -> {
                with(binding) {
                    ivStarOne.setImageResource(R.drawable.ic_star_empty)
                    ivStarTwo.setImageResource(R.drawable.ic_star_empty)
                    ivStarThree.setImageResource(R.drawable.ic_star_empty)
                    ivStarFour.setImageResource(R.drawable.ic_star_empty)
                    ivStarFive.setImageResource(R.drawable.ic_star_empty)
                }

            }
        }
    }
}