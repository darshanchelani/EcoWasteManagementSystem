package com.ecowaste.app.screens

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ecowaste.app.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile_details -> Toast.makeText(context, "Profile Details Clicked", Toast.LENGTH_SHORT).show()
            R.id.bank_details -> Toast.makeText(context, "Bank Details Clicked", Toast.LENGTH_SHORT).show()
            R.id.benefits_of_waste_management -> Toast.makeText(context, "Benefits Clicked", Toast.LENGTH_SHORT).show()
            R.id.check_your_pickup_status -> Toast.makeText(context, "Pickup Status Clicked", Toast.LENGTH_SHORT).show()
            R.id.logout -> Toast.makeText(context, "Logout Clicked", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}