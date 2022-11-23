package com.app.wildtreasure.ui.pres

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.wildtreasure.databinding.ActGBinding
import com.app.wildtreasure.ui.theme.GViewModel

class GAG : AppCompatActivity() {

    private lateinit var binding: ActGBinding
    private val viewModel: GViewModel by viewModels()
    private val adapter = RVAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActGBinding.inflate(layoutInflater)
        setContentView(binding.root)

        iO()
        iA()

        binding.btnPlay.setOnClickListener {
            viewModel.addPosition()
        }

        binding.animationView.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}

            override fun onAnimationEnd(p0: Animator) {
                binding.animationView.visibility = View.GONE

            }
        })
        binding.btnAddBalance.setOnClickListener{
            viewModel.addMoney()
            binding.animationView.visibility = View.VISIBLE
            binding.animationView.playAnimation()

        }


    }

    private fun iO() {

        viewModel.items.observe(this) {
            adapter.itemList.clear()
            adapter.itemList.addAll(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.credits.observe(this){
            binding.txtViewBalance.text = "Your credits: $it"

        }

    }

    private fun iA() {
        val myLinearLayoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        binding.cardItemRV.layoutManager = myLinearLayoutManager
        binding.cardItemRV.adapter = adapter

        viewModel.position.observe(this) { positionInt ->
            (binding.cardItemRV.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(
                    positionInt,
                    0
                )
        }
    }


}