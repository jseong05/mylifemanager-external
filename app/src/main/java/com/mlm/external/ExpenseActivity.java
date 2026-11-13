package com.mlm.external;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mlm.external.databinding.ActivityExpenseBinding;
import com.mlm.external.databinding.DialogAddExpenseBinding;
import java.util.List;
import java.util.UUID;

public class ExpenseActivity extends AppCompatActivity {
    private ActivityExpenseBinding binding;
    private MyDataStore dataManager;
    private ExpenseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataManager = new MyDataStore(this);

        binding.rvExpenseList.setLayoutManager(new LinearLayoutManager(this));
        List<Expense> list = dataManager.getExpenses();
        updateEmptyState(list);
        updateWasteReport();
        adapter = new ExpenseAdapter(list, (expense, position) -> {
            showAddOrEditExpenseDialog(expense, position);
        });
        binding.rvExpenseList.setAdapter(adapter);

        // 필터 칩 리스너
        binding.chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            filterExpenses(binding.etSearchExpense.getText().toString().trim());
        });

        // 카테고리 통계 다이어로그 버튼
        binding.btnCategoryStats.setOnClickListener(v -> showCategoryStatsDialog());

        // 11/05
        // 가계부 내역 키워드 실시간 검색 (TextWatcher 연동)
        binding.etSearchExpense.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExpenses(s.toString().trim());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // 11/05
            // 검색 필터링 상태에서도 고유 ID 기반으로 정확한 지출 내역을 삭제하도록 수정 (인덱스 불일치 버그 해결)
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Expense targetExpense = adapter.getItem(position);
                if (targetExpense != null) {
                    List<Expense> currentList = dataManager.getExpenses();
                    for (int i = 0; i < currentList.size(); i++) {
                        if (currentList.get(i).getId().equals(targetExpense.getId())) {
                            currentList.remove(i);
                            break;
                        }
                    }
                    dataManager.saveExpenses(currentList);
                    filterExpenses(binding.etSearchExpense.getText().toString().trim());
                    updateWasteReport();
                    Toast.makeText(ExpenseActivity.this, "내역이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(binding.rvExpenseList);

        
        binding.fabAddExpense.setOnClickListener(v -> showAddOrEditExpenseDialog(null, -1));

        binding.bottomNavigation.setSelectedItemId(R.id.navigation_expense);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.navigation_schedule) {
                Intent intent = new Intent(this, ScheduleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
                return true;
            }
            return id == R.id.navigation_expense;
        });
    }

    private void filterExpenses(String query) {
        List<Expense> allList = dataManager.getExpenses();
        List<Expense> filtered = new java.util.ArrayList<>();

        int checkedId = binding.chipGroupFilter.getCheckedChipId();

        for (Expense e : allList) {
            boolean passChip = true;
            if (checkedId == R.id.chipExpenseOnly) {
                passChip = e.isMinus();
            } else if (checkedId == R.id.chipIncomeOnly) {
                passChip = !e.isMinus();
            } else if (checkedId == R.id.chipWasteOnly) {
                passChip = e.isMinus() && e.isWaste();
            }

            if (!passChip) continue;

            if (query != null && !query.isEmpty()) {
                boolean matchMemo = e.getMemo() != null && e.getMemo().toLowerCase().contains(query.toLowerCase());
                boolean matchCat = e.getCategory() != null && e.getCategory().toLowerCase().contains(query.toLowerCase());
                if (!matchMemo && !matchCat) continue;
            }

            filtered.add(e);
        }
        adapter.updateData(filtered);
        updateEmptyState(filtered);
    }

    private void showCategoryStatsDialog() {
        List<Expense> allList = dataManager.getExpenses();
        java.util.Map<String, Integer> categorySums = new java.util.HashMap<>();
        int totalExpense = 0;

        for (Expense e : allList) {
            if (e.isMinus()) {
                String cat = e.getCategory();
                int amt = e.getMoney();
                Integer current = categorySums.get(cat);
                int amtSum = (current != null ? current : 0) + amt;
                categorySums.put(cat, amtSum);
                totalExpense += amt;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(java.util.Locale.getDefault(), "총 지출 금액: %,d원\n\n[카테고리별 지출 비율]\n", totalExpense));

        if (categorySums.isEmpty()) {
            sb.append("지출 내역이 없습니다.");
        } else {
            for (java.util.Map.Entry<String, Integer> entry : categorySums.entrySet()) {
                String cat = entry.getKey();
                int amt = entry.getValue();
                int percent = totalExpense > 0 ? (amt * 100) / totalExpense : 0;
                sb.append(String.format(java.util.Locale.getDefault(), "• %s: %,d원 (%d%%)\n", cat, amt, percent));
            }
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("📈 카테고리별 지출 통계")
                .setMessage(sb.toString())
                .setPositiveButton("확인", null)
                .show();
    }

    private void updateEmptyState(List<Expense> list) {
        boolean empty = list == null || list.isEmpty();
        if (binding != null) {
            binding.tvEmptyExpense.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.rvExpenseList.setVisibility(empty ? View.GONE : View.VISIBLE);
        }
    }

    private void updateWasteReport() {
        // 11/04
        // 이번 달 낭비 챌린지 지출 건수 및 누적 금액 리포트 업데이트 (strings.xml 리소스 연동)
        if (binding != null && binding.tvWasteValue != null) {
            int count = dataManager.getMonthlyWasteCount();
            int amount = dataManager.getMonthlyWasteAmount();
            if (count > 0) {
                String formattedAmount = String.format(java.util.Locale.getDefault(), "%,d", amount);
                binding.tvWasteValue.setText(getString(R.string.waste_report_format, count, formattedAmount));
            } else {
                binding.tvWasteValue.setText(getString(R.string.waste_report_zero));
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void showAddOrEditExpenseDialog(Expense editExpense, int position) {
        DialogAddExpenseBinding dialogBinding = DialogAddExpenseBinding.inflate(LayoutInflater.from(this));

        String[] categories = new String[]{"식비 🍔", "교통 🚌", "쇼핑 🛍️", "문화 🎬", "기타 📦"};
        String[] categoryKeys = new String[]{"식비", "교통", "쇼핑", "문화", "기타"};
        android.widget.ArrayAdapter<String> catAdapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories
        );
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spExpenseCategory.setAdapter(catAdapter);

        if (editExpense != null) {
            dialogBinding.etExpenseTitle.setText(editExpense.getMemo());
            dialogBinding.etExpenseAmount.setText(String.valueOf(editExpense.getMoney()));
            if (editExpense.isMinus()) {
                dialogBinding.rbExpense.setChecked(true);
            } else {
                dialogBinding.rbIncome.setChecked(true);
            }
            dialogBinding.cbIsWaste.setChecked(editExpense.isWaste());

            String currentCat = editExpense.getCategory();
            for (int i = 0; i < categoryKeys.length; i++) {
                if (categoryKeys[i].equalsIgnoreCase(currentCat)) {
                    dialogBinding.spExpenseCategory.setSelection(i);
                    break;
                }
            }
        }

        String titleText = (editExpense == null) ? "내역 추가" : "내역 수정";
        String positiveText = (editExpense == null) ? "추가" : "수정";

        new MaterialAlertDialogBuilder(this)
                .setTitle(titleText)
                .setView(dialogBinding.getRoot())
                .setPositiveButton(positiveText, (dialog, which) -> {
                    String memoStr = dialogBinding.etExpenseTitle.getText().toString().trim();
                    String moneyStr = dialogBinding.etExpenseAmount.getText().toString().trim();

                    if (memoStr.isEmpty() || moneyStr.isEmpty()) {
                        Toast.makeText(this, "입력을 다 안했어요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int moneyValue;
                    try {
                        moneyValue = (int) Double.parseDouble(moneyStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "올바른 금액을 입력해 주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean isMinusValue = dialogBinding.rbExpense.isChecked();
                    boolean isWasteValue = dialogBinding.cbIsWaste.isChecked();
                    int selectedCatPos = dialogBinding.spExpenseCategory.getSelectedItemPosition();
                    String selectedCategory = (selectedCatPos >= 0 && selectedCatPos < categoryKeys.length) ? categoryKeys[selectedCatPos] : "기타";

                    List<Expense> currentList = dataManager.getExpenses();
                    if (editExpense == null) {
                        Expense newExpense = new Expense(UUID.randomUUID().toString(), memoStr, moneyValue, isMinusValue, isWasteValue, System.currentTimeMillis(), selectedCategory);
                        currentList.add(newExpense);
                        Toast.makeText(this, "내역이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Expense updatedExpense = new Expense(editExpense.getId(), memoStr, moneyValue, isMinusValue, isWasteValue, editExpense.getTimestamp(), selectedCategory);
                        for (int i = 0; i < currentList.size(); i++) {
                            if (currentList.get(i).getId().equals(editExpense.getId())) {
                                currentList.set(i, updatedExpense);
                                break;
                            }
                        }
                        Toast.makeText(this, "내역이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                    dataManager.saveExpenses(currentList);
                    filterExpenses(binding.etSearchExpense.getText().toString().trim());
                    updateWasteReport();

                    if (dataManager.totalMoneyNow() < 0) {
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("진심이냐?")
                                .setMessage("잔액이 마이너스인데 지금 이걸 또 쓰는 거냐고!! 정신 차려라 진짜...")
                                .setPositiveButton("반성합니다", null)
                                .show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
