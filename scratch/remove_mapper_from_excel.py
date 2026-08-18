import openpyxl, re, copy, shutil
from openpyxl.styles import Font, PatternFill, Alignment, Border, Side
from openpyxl.chart import PieChart, Reference

EXCEL_PATH = 'docs/SEP490_G81_Report5.1_Unit Test.xlsx'
BACKUP_PATH = 'docs/SEP490_G81_Report5.1_Unit Test_BACKUP.xlsx'

def copy_cell_style(src_cell, dst_cell):
    if src_cell.has_style:
        dst_cell.font = copy.copy(src_cell.font)
        dst_cell.border = copy.copy(src_cell.border)
        dst_cell.fill = copy.copy(src_cell.fill)
        dst_cell.number_format = src_cell.number_format
        dst_cell.protection = copy.copy(src_cell.protection)
        dst_cell.alignment = copy.copy(src_cell.alignment)

def remove_mappers(file_path):
    wb = openpyxl.load_workbook(file_path, data_only=False)

    # 1. Sheets to delete
    sheets_to_del = ['domainToResultAwsCost', 'domainToResultNotification']
    for s in sheets_to_del:
        if s in wb.sheetnames:
            del wb[s]
            print(f'Deleted sheet: {s}')

    # 2. Update Cover Sheet
    cov = wb['Cover']
    cov_rows = []
    # Read Record of change table rows (rows 11 to 82)
    for r in range(11, 85):
        vals = [cov.cell(r, c).value for c in range(1, 7)]
        if any(vals):
            change_item = str(vals[2] or '')
            change_desc = str(vals[4] or '')
            if 'domainToResult' in change_item or 'fromDomain' in change_item or 'domainToResult' in change_desc or 'fromDomain' in change_desc:
                print(f'Cover: Skipping mapper row {r}: {change_item}')
                continue
            cov_rows.append(vals)

    print(f'Cover: Remaining rows {len(cov_rows)}')

    # Save sample style from row 11
    sample_cov_styles = [copy.copy(cov.cell(11, c)) for c in range(1, 7)]

    # Clear old rows 11 to 90
    for r in range(11, 90):
        for c in range(1, 10):
            cell = cov.cell(r, c)
            cell.value = None
            cell.border = Border()
            cell.fill = PatternFill(fill_type=None)

    # Rewrite Cover rows
    for idx, vals in enumerate(cov_rows):
        r = 11 + idx
        for c_idx, val in enumerate(vals):
            c = c_idx + 1
            cell = cov.cell(r, c, val)
            copy_cell_style(sample_cov_styles[c_idx], cell)

    # 3. Update MethodList Sheet
    ml = wb['MethodList']
    ml_rows = []
    for r in range(11, 89):
        no = ml.cell(r, 1).value
        mod = ml.cell(r, 2).value
        mname = ml.cell(r, 3).value
        s_link = ml.cell(r, 4).value
        desc = ml.cell(r, 5).value
        pre = ml.cell(r, 6).value

        sheet_name = s_link
        if sheet_name and isinstance(sheet_name, str) and '#' in sheet_name:
            m = re.search(r'#([A-Za-z0-9_]+)!', sheet_name)
            if m:
                sheet_name = m.group(1)

        if sheet_name in sheets_to_del or mname in ['domainToResult', 'fromDomain']:
            print(f'MethodList: Skipping mapper row {r}: {mod} | {mname} | {sheet_name}')
            continue

        if no is not None and mname is not None:
            ml_rows.append({
                'mod': mod,
                'mname': mname,
                'sheet_name': sheet_name,
                'desc': desc,
                'pre': pre
            })

    print(f'MethodList: Remaining rows {len(ml_rows)}')

    sample_ml_styles = [copy.copy(ml.cell(11, c)) for c in range(1, 7)]

    # Clear old rows in MethodList
    for r in range(11, 95):
        for c in range(1, 10):
            cell = ml.cell(r, c)
            cell.value = None
            cell.border = Border()
            cell.fill = PatternFill(fill_type=None)

    # Rewrite MethodList
    for idx, rdata in enumerate(ml_rows):
        r = 11 + idx
        no = idx + 1
        s_name = rdata['sheet_name']
        row_vals = [
            no,
            rdata['mod'],
            rdata['mname'],
            f'=HYPERLINK("#{s_name}!A1", "{s_name}")',
            rdata['desc'],
            rdata['pre']
        ]
        for c_idx, val in enumerate(row_vals):
            c = c_idx + 1
            cell = ml.cell(r, c, val)
            copy_cell_style(sample_ml_styles[c_idx], cell)

    # 4. Update Statistics Sheet
    stats = wb['Statistics']

    # Sample styles from row 12, row 89 (subtotal), rows 91..95 (kpi)
    sample_stat_data_styles = [copy.copy(stats.cell(12, c)) for c in range(1, 10)]
    sample_subtotal_styles = [copy.copy(stats.cell(89, c)) for c in range(1, 10)]
    sample_kpi_styles = {}
    for r_kpi in range(91, 96):
        sample_kpi_styles[r_kpi] = [copy.copy(stats.cell(r_kpi, c)) for c in range(1, 10)]

    # Clear old data rows in Statistics from 12 to 105
    for r in range(12, 105):
        for c in range(1, 10):
            cell = stats.cell(r, c)
            cell.value = None
            cell.border = Border()
            cell.fill = PatternFill(fill_type=None)

    # Write 76 method rows (rows 12 to 87)
    for idx, rdata in enumerate(ml_rows):
        r = 12 + idx
        no = idx + 1
        ml_r = 11 + idx
        s_name = rdata['sheet_name']
        
        row_vals = [
            no,
            f'=HYPERLINK("#{s_name}!A1", MethodList!C{ml_r})',
            f'={s_name}!A5',
            f'={s_name}!C5',
            f'={s_name}!F5',
            f'={s_name}!L5',
            f'={s_name}!M5',
            f'={s_name}!N5',
            f'=SUM(F{r}:H{r})'
        ]
        for c_idx, val in enumerate(row_vals):
            c = c_idx + 1
            cell = stats.cell(r, c, val)
            copy_cell_style(sample_stat_data_styles[c_idx], cell)

    last_method_row = 12 + len(ml_rows) - 1  # 87
    subtotal_row = last_method_row + 1      # 88

    # Write Sub total row (row 88)
    subtotal_vals = [
        None,
        'Sub total',
        f'=SUM(C12:C{last_method_row})',
        f'=SUM(D12:D{last_method_row})',
        f'=SUM(E12:E{last_method_row})',
        f'=SUM(F12:F{last_method_row})',
        f'=SUM(G12:G{last_method_row})',
        f'=SUM(H12:H{last_method_row})',
        f'=SUM(I12:I{last_method_row})'
    ]
    for c_idx, val in enumerate(subtotal_vals):
        c = c_idx + 1
        cell = stats.cell(subtotal_row, c, val)
        copy_cell_style(sample_subtotal_styles[c_idx], cell)

    # Write KPI rows (rows 90 to 94)
    kpi_rows_data = [
        (subtotal_row + 2, [None, 'Test coverage', None, f'=(C{subtotal_row}+D{subtotal_row})*100/(I{subtotal_row})', '%', None, None, None, None], 91),
        (subtotal_row + 3, [None, 'Test successful coverage', None, f'=C{subtotal_row}*100/(I{subtotal_row})', '%', None, None, None, None], 92),
        (subtotal_row + 4, [None, 'Normal case', None, f'=F{subtotal_row}*100/I{subtotal_row}', '%', None, None, None, None], 93),
        (subtotal_row + 5, [None, 'Abnormal case', None, f'=G{subtotal_row}*100/I{subtotal_row}', '%', None, None, None, None], 94),
        (subtotal_row + 6, [None, 'Boundary case', None, f'=H{subtotal_row}*100/I{subtotal_row}', '%', None, None, None, None], 95)
    ]

    for curr_r, row_vals, old_kpi_r in kpi_rows_data:
        for c_idx, val in enumerate(row_vals):
            c = c_idx + 1
            cell = stats.cell(curr_r, c, val)
            copy_cell_style(sample_kpi_styles[old_kpi_r][c_idx], cell)

    # Update chart references
    for i, chart in enumerate(stats._charts):
        for s in chart.series:
            if i == 0:  # Test Type (N, A, B)
                if hasattr(s, 'val') and s.val and hasattr(s.val, 'numRef') and s.val.numRef:
                    s.val.numRef.f = f'Statistics!$F${subtotal_row}:$H${subtotal_row}'
            elif i == 1:  # Passed Percent (Passed, Failed, Untested)
                if hasattr(s, 'val') and s.val and hasattr(s.val, 'numRef') and s.val.numRef:
                    s.val.numRef.f = f'Statistics!$C${subtotal_row}:$E${subtotal_row}'

    wb.save(file_path)
    print(f'Successfully updated {file_path}!')

if __name__ == '__main__':
    remove_mappers(EXCEL_PATH)
