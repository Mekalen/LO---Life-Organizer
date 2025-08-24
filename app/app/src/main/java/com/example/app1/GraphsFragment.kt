package com.example.app1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.app1.databinding.FragmentGraphsBinding

class GraphsFragment : Fragment() {
    private var _binding: FragmentGraphsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).updateToolbarTitle(getString(R.string.nav_graphs))

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf(getString(R.string.chart_pie), getString(R.string.chart_bar), getString(R.string.chart_line))
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerChartType.adapter = adapter

        binding.buttonGenerate.setOnClickListener {
            val selection = binding.spinnerChartType.selectedItemPosition
            when (selection) {
                0 -> binding.textPlaceholder.text = "Pie chart preview here"
                1 -> binding.textPlaceholder.text = "Bar chart preview here"
                2 -> binding.textPlaceholder.text = "Line chart preview here"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
