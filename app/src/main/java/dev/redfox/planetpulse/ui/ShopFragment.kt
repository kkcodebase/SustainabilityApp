package dev.redfox.planetpulse.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.redfox.planetpulse.ui.ShopItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.redfox.planetpulse.R
import dev.redfox.planetpulse.databinding.FragmentShopBinding
import dev.redfox.planetpulse.databinding.ItemShopBinding

class ShopFragment : Fragment() {

    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!
    private lateinit var shopAdapter: ShopAdapter
    private val shopItems = mutableListOf<ShopItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updatePointsDisplay()
        setupShopItems()
    }

    private fun updatePointsDisplay() {
        val sharedPreferences = requireActivity().getSharedPreferences("EcoPointsPrefs", Context.MODE_PRIVATE)
        val points = sharedPreferences.getInt("eco_points", 0)
        binding.tvAvailablePoints.text = "Available Points: $points"
    }

    override fun onResume() {
        super.onResume()
        updatePointsDisplay()

        // Play background video
        val uri = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.videodashboard}")
        binding.shopVideo.setVideoURI(uri)
        binding.shopVideo.setOnPreparedListener { it.isLooping = true }
        binding.shopVideo.start()
    }

    private fun setupShopItems() {
        shopItems.clear()
        shopItems.addAll(
            listOf(
                ShopItem(1, "Reusable Water Bottle", "Eco-friendly stainless steel bottle", 500, R.drawable.baseline_water_drop_24),
                ShopItem(2, "Reusable Masks", "Pack of 4 reusable masks", 300, R.drawable.baseline_masks_24),
                ShopItem(3, "Solar Power Bank", "10000mAh solar-powered charger", 1000, R.drawable.baseline_electric_bolt_24),
                ShopItem(4, "Organic Cotton Tote Bag", "Durable shopping bag made from organic cotton", 200, R.drawable.baseline_shopping_bag_24),
                ShopItem(5, "LED Light Bulb Pack", "Set of 4 energy-efficient LED bulbs", 400, R.drawable.baseline_light_24),
                ShopItem(6, "Reusable Produce Bags", "Set of 5 mesh bags for grocery shopping", 350, R.drawable.baseline_shopping_bag_24),
                ShopItem(7, "Stainless Steel Straws", "Pack of 4 reusable straws with cleaning brush", 150, R.drawable.baseline_brush_24),
                ShopItem(8, "Compost Bin", "Kitchen compost bin for food scraps", 600, R.drawable.baseline_delete_outline_24),
                ShopItem(9, "Beeswax Food Wraps", "Set of 3 reusable food wraps", 450, R.drawable.baseline_food_bank_24),
                ShopItem(10, "Recycled Paper Notebook", "Notebook made from 100% recycled paper", 250, R.drawable.baseline_menu_book_24)
            )
        )

        shopAdapter = ShopAdapter(shopItems) { item ->
            redeemItem(item)
        }

        binding.rvShopItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = shopAdapter
        }
    }

    private fun redeemItem(item: ShopItem) {
        val sharedPreferences = requireActivity().getSharedPreferences("EcoPointsPrefs", Context.MODE_PRIVATE)
        val currentPoints = sharedPreferences.getInt("eco_points", 0)

        if (currentPoints >= item.pointsCost) {
            val newPoints = currentPoints - item.pointsCost
            sharedPreferences.edit().putInt("eco_points", newPoints).apply()
            updatePointsDisplay()
            Toast.makeText(context, "You've redeemed ${item.name}!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Not enough points to redeem this item", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ShopFragment()
    }
}


class ShopAdapter(
    private val items: List<ShopItem>,
    private val onItemClick: (ShopItem) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {

    inner class ShopViewHolder(private val binding: ItemShopBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShopItem) {
            binding.tvItemName.text = item.name
            binding.tvItemDescription.text = item.description
            binding.tvPointsCost.text = "${item.pointsCost} points"
            binding.ivItemImage.setImageResource(item.imageResId)
            binding.btnRedeem.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val binding = ItemShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
