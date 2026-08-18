import openpyxl
from openpyxl.styles import Font, PatternFill, Alignment, Border, Side
from openpyxl.chart import PieChart, Reference
from openpyxl.chart.label import DataLabelList

EXCEL_PATH = 'docs/SEP490_G81_Report5.1_Unit Test.xlsx'

def add_charts_to_statistics():
    wb = openpyxl.load_workbook(EXCEL_PATH)
    ws = wb['Statistics']

    # Clear existing charts
    ws._charts.clear()

    # Find last method row (should be 86 for 76 methods, row 11 to 86)
    last_method_row = 11 + 76 - 1  # 86
    total_row = last_method_row + 1  # 87

    font_bold = Font(name='Tahoma', size=8, bold=True, color='FF000000')
    font_regular = Font(name='Tahoma', size=8, bold=False, color='FF000000')
    font_header_white = Font(name='Tahoma', size=8, bold=True, color='FFFFFFFF')
    font_title = Font(name='Tahoma', size=10, bold=True, color='FF000000')

    fill_navy = PatternFill(fill_type='solid', start_color='FF000080', end_color='FF000080')
    fill_header_gray = PatternFill(fill_type='solid', start_color='FFE0E0E0', end_color='FFE0E0E0')
    fill_white = PatternFill(fill_type='solid', start_color='FFFFFFFF', end_color='FFFFFFFF')

    thin_border_side = Side(border_style='thin', color='FF000000')
    double_border_side = Side(border_style='double', color='FF000000')

    border_total = Border(
        left=thin_border_side,
        right=thin_border_side,
        top=thin_border_side,
        bottom=double_border_side
    )
    border_cell = Border(
        left=thin_border_side,
        right=thin_border_side,
        top=thin_border_side,
        bottom=thin_border_side
    )

    # 1. Row 87: Total row
    ws.cell(total_row, 1, None)
    ws.cell(total_row, 2, 'Total')
    ws.cell(total_row, 3, f'=SUM(C11:C{last_method_row})')
    ws.cell(total_row, 4, f'=SUM(D11:D{last_method_row})')
    ws.cell(total_row, 5, f'=SUM(E11:E{last_method_row})')
    ws.cell(total_row, 6, f'=SUM(F11:F{last_method_row})')
    ws.cell(total_row, 7, f'=SUM(G11:G{last_method_row})')
    ws.cell(total_row, 8, f'=SUM(H11:H{last_method_row})')
    ws.cell(total_row, 9, f'=SUM(I11:I{last_method_row})')

    for c in range(1, 10):
        cell = ws.cell(total_row, c)
        cell.font = font_bold
        cell.fill = fill_header_gray
        cell.border = border_total
        cell.alignment = Alignment(horizontal='center' if c != 2 else 'left', vertical='center')

    # 2. Clear rows 89 to 120 around bottom
    for r in range(total_row + 1, total_row + 30):
        for c in range(1, 15):
            ws.cell(r, c).value = None
            ws.cell(r, c).fill = fill_white
            ws.cell(r, c).border = Border()

    # 3. Summary Tables below the main grid
    # Table A: Test Result Status (Rows 90-93)
    r_stat1 = total_row + 3 # 90
    ws.cell(r_stat1 - 1, 2, 'Test Result Status')
    ws.cell(r_stat1 - 1, 2).font = font_title

    ws.cell(r_stat1, 2, 'Status')
    ws.cell(r_stat1, 3, 'Count')
    ws.cell(r_stat1, 4, 'Percentage')
    for c in [2, 3, 4]:
        cell = ws.cell(r_stat1, c)
        cell.font = font_header_white
        cell.fill = fill_navy
        cell.alignment = Alignment(horizontal='center', vertical='center')
        cell.border = border_cell

    stat1_data = [
        ('Passed', f'=C{total_row}', f'=C{total_row}/I{total_row}'),
        ('Failed', f'=D{total_row}', f'=D{total_row}/I{total_row}'),
        ('Untested', f'=E{total_row}', f'=E{total_row}/I{total_row}')
    ]
    for idx, (label, cnt_fml, pct_fml) in enumerate(stat1_data):
        curr_r = r_stat1 + 1 + idx
        ws.cell(curr_r, 2, label)
        ws.cell(curr_r, 3, cnt_fml)
        ws.cell(curr_r, 4, pct_fml)

        ws.cell(curr_r, 2).font = font_bold
        ws.cell(curr_r, 2).alignment = Alignment(horizontal='left', vertical='center')
        ws.cell(curr_r, 2).border = border_cell

        ws.cell(curr_r, 3).font = font_regular
        ws.cell(curr_r, 3).alignment = Alignment(horizontal='center', vertical='center')
        ws.cell(curr_r, 3).border = border_cell

        ws.cell(curr_r, 4).font = font_regular
        ws.cell(curr_r, 4).alignment = Alignment(horizontal='center', vertical='center')
        ws.cell(curr_r, 4).number_format = '0.0%'
        ws.cell(curr_r, 4).border = border_cell

    # Table B: Test Case Type Distribution (Rows 90-93, Cols F:H)
    ws.cell(r_stat1 - 1, 6, 'Test Case Type Distribution')
    ws.cell(r_stat1 - 1, 6).font = font_title

    ws.cell(r_stat1, 6, 'Type')
    ws.cell(r_stat1, 7, 'Count')
    ws.cell(r_stat1, 8, 'Percentage')
    for c in [6, 7, 8]:
        cell = ws.cell(r_stat1, c)
        cell.font = font_header_white
        cell.fill = fill_navy
        cell.alignment = Alignment(horizontal='center', vertical='center')
        cell.border = border_cell

    stat2_data = [
        ('Normal (N)', f'=F{total_row}', f'=F{total_row}/I{total_row}'),
        ('Abnormal (A)', f'=G{total_row}', f'=G{total_row}/I{total_row}'),
        ('Boundary (B)', f'=H{total_row}', f'=H{total_row}/I{total_row}')
    ]
    for idx, (label, cnt_fml, pct_fml) in enumerate(stat2_data):
        curr_r = r_stat1 + 1 + idx
        ws.cell(curr_r, 6, label)
        ws.cell(curr_r, 7, cnt_fml)
        ws.cell(curr_r, 8, pct_fml)

        ws.cell(curr_r, 6).font = font_bold
        ws.cell(curr_r, 6).alignment = Alignment(horizontal='left', vertical='center')
        ws.cell(curr_r, 6).border = border_cell

        ws.cell(curr_r, 7).font = font_regular
        ws.cell(curr_r, 7).alignment = Alignment(horizontal='center', vertical='center')
        ws.cell(curr_r, 7).border = border_cell

        ws.cell(curr_r, 8).font = font_regular
        ws.cell(curr_r, 8).alignment = Alignment(horizontal='center', vertical='center')
        ws.cell(curr_r, 8).number_format = '0.0%'
        ws.cell(curr_r, 8).border = border_cell

    # 4. Create Chart 1: Test Result Status (PieChart)
    pie1 = PieChart()
    pie1.title = "Test Result Status"
    labels1 = Reference(ws, min_col=2, min_row=r_stat1 + 1, max_row=r_stat1 + 3)
    data1 = Reference(ws, min_col=3, min_row=r_stat1, max_row=r_stat1 + 3)
    pie1.add_data(data1, titles_from_data=True)
    pie1.set_categories(labels1)
    pie1.dataLabels = DataLabelList()
    pie1.dataLabels.showPercent = True
    pie1.dataLabels.showVal = False
    pie1.width = 16
    pie1.height = 9.5
    ws.add_chart(pie1, "K11")

    # 5. Create Chart 2: Test Case Type Distribution (PieChart)
    pie2 = PieChart()
    pie2.title = "Test Case Type Distribution"
    labels2 = Reference(ws, min_col=6, min_row=r_stat1 + 1, max_row=r_stat1 + 3)
    data2 = Reference(ws, min_col=7, min_row=r_stat1, max_row=r_stat1 + 3)
    pie2.add_data(data2, titles_from_data=True)
    pie2.set_categories(labels2)
    pie2.dataLabels = DataLabelList()
    pie2.dataLabels.showPercent = True
    pie2.dataLabels.showVal = False
    pie2.width = 16
    pie2.height = 9.5
    ws.add_chart(pie2, "K30")

    wb.save(EXCEL_PATH)
    print("Cleanly saved Statistics sheet with exactly 2 PieCharts!")

if __name__ == '__main__':
    add_charts_to_statistics()
