package licenta.allbank.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import licenta.allbank.R;
import licenta.allbank.data.adapter.BudgetAdapter;
import licenta.allbank.data.adapter.TransactionCategoryOptionAdapter;
import licenta.allbank.data.model.allbank.others.CategoryOption;
import licenta.allbank.data.model.database.Budget;
import licenta.allbank.utils.click_interface.ClickInterfaceCategory;
import licenta.allbank.utils.click_interface.ClickLongClickInterface;

import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;
import static licenta.allbank.ui.statistics.StatisticsCategoryFragment.CATEGORY_ALL;
import static licenta.allbank.ui.statistics.StatisticsCategoryFragment.allCategoriesStringList;
import static licenta.allbank.ui.statistics.StatisticsCategoryFragment.copyCategoryListObjects;

public class StatisticsBudgetingFragment extends Fragment implements ClickLongClickInterface, ClickInterfaceCategory {
    public static final String STATISTICS_BUDGET = "STATISTICS_BUDGET";
    public static final String BUDGET = "BUDGET";

    private BudgetAdapter budgetAdapter;
    private TransactionCategoryOptionAdapter transactionCategoryOptionAdapterFilter;

    private FloatingActionButton fab;
    private RecyclerView recyclerViewBudget, recyclerViewCategoryFilter;
    private StatisticsBudgetingViewModel statisticsBudgetingViewModel;

    private NavController navController;
    /* List with the current chosen categories*/
    public static List<String> categorySelectedList = new ArrayList<>();
    /* List with the category options  */
    private static List<CategoryOption> categoryOptions = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statisticsBudgetingViewModel = new ViewModelProvider(this).get(StatisticsBudgetingViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_statistics_budgeting, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init default values */
        initDefaultValues();

        /* Init the buttons behaviour */
        initBehaviours();
        
        return view;
    }

    private void initUiElements(View view) {
        recyclerViewBudget = view.findViewById(R.id.recycler_view_statistics_budgeting_budget);
        recyclerViewCategoryFilter = view.findViewById(R.id.recycler_view_statistics_budgeting_category_filter);
        fab = view.findViewById(R.id.fab_statistics_budgeting);
    }

    private void initBehaviours() {
        initRecyclerViewCategoryFilter();
        initRecyclerViewBudgets();
        initFab();
    }

    private void initDefaultValues() {
        /* Display data on recycler view */
        statisticsBudgetingViewModel.getCurrentBudgets().observe(getViewLifecycleOwner(), budgetList -> budgetAdapter.setBudgets(budgetList));
        /* Make a new copy of the CategoryOption list */
        categoryOptions = copyCategoryListObjects();
    }

    private void initRecyclerViewCategoryFilter() {
        setLayoutManagerRecyclerViewCategoryFilter();
        setAdapterRecyclerViewCategoryFilter();
    }

    private void initRecyclerViewBudgets() {
        setLayoutManagerRecyclerViewBudgets();
        setAdapterRecyclerViewBudgets();
    }

    private void initFab() {
        fab.setOnClickListener(v -> {
            navigateToNextFragment(navController, R.id.action_navigation_statistics_to_newBudget, null);
        });
    }

    private void setLayoutManagerRecyclerViewCategoryFilter() {
        recyclerViewCategoryFilter.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    private void setLayoutManagerRecyclerViewBudgets() {
        recyclerViewBudget.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    private void setAdapterRecyclerViewCategoryFilter() {
        transactionCategoryOptionAdapterFilter = new TransactionCategoryOptionAdapter(getContext(), categoryOptions, this, BUDGET);
        recyclerViewCategoryFilter.setAdapter(transactionCategoryOptionAdapterFilter);
    }

    private void setAdapterRecyclerViewBudgets() {
        budgetAdapter = new BudgetAdapter(getContext(), this, this);
        recyclerViewBudget.setAdapter(budgetAdapter);
    }

    @Override
    public void onClick(int position) {
        /* Open new fragment */
        Budget budget = budgetAdapter.getBudget(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUDGET, budget);
        navigateToNextFragment(navController, R.id.action_navigation_statistics_to_budgetFragment2, bundle);
    }

    @Override
    public void onLongClick(int position) {
        /* Edit Budget*/
        Budget budget = budgetAdapter.getBudget(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUDGET, budget);
        navigateToNextFragment(navController, R.id.action_navigation_statistics_to_editBudgetFragment, bundle);
    }

    @Override
    public void onClickCategory(int position) {
        /* If the selected category is not the ALL category, then update it */
        CategoryOption allCategoryOption = transactionCategoryOptionAdapterFilter.getCategoryOption(0);
        if (position != 0) {
            if (allCategoryOption.isSelected()) {
                allCategoryOption.setSelected(false);
                categorySelectedList.remove(CATEGORY_ALL);
            }

            CategoryOption categoryOption = transactionCategoryOptionAdapterFilter.getCategoryOption(position);
            boolean selected = categoryOption.isSelected();
            if (selected && (categorySelectedList.size() >= 3 && categorySelectedList.contains(CATEGORY_ALL) || categorySelectedList.size() >= 2 && !categorySelectedList.contains(CATEGORY_ALL))) {
                categorySelectedList.remove(categoryOption.getCategoryType());
                categoryOption.setSelected(false);
            } else if (!selected) {
                categorySelectedList.add(categoryOption.getCategoryType());
                categoryOption.setSelected(true);
            }
        } else {
            /* When the selected category is the ALL one, then clear the others */
            if (!allCategoryOption.isSelected()) {
                for (CategoryOption categoryOption : categoryOptions) {
                    categoryOption.setSelected(false);
                }
                allCategoryOption.setSelected(true);
                categorySelectedList.clear();
                categorySelectedList.add(CATEGORY_ALL);
            }
        }

        /* Update category recycler and charts data */
        transactionCategoryOptionAdapterFilter.updateUI();
        displayFilteredBudgets();
    }

    /**
     * Function that displays the filtered budgets
     */
    private void displayFilteredBudgets() {
        /* Check whether to show all categories or just part of it */
        List<String> categorySelectedList = getSelectedCategoryList();
        statisticsBudgetingViewModel.getFilteredBudgets(categorySelectedList).observe(getViewLifecycleOwner(), budgetList -> budgetAdapter.setBudgets(budgetList));
    }

    /**
     * @return list of Strings containing the selected transaction categories
     */
    private List<String> getSelectedCategoryList() {
        if (categorySelectedList.get(0).equalsIgnoreCase(CATEGORY_ALL)) {
            return allCategoriesStringList;
        }
        return categorySelectedList;
    }
}
