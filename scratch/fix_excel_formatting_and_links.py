import openpyxl
from openpyxl.styles import Font, PatternFill, Alignment, Border, Side
from openpyxl.utils import get_column_letter
from openpyxl.worksheet.datavalidation import DataValidation
import datetime

EXCEL_PATH = 'docs/SEP490_G81_Report5.1_Unit Test.xlsx'

# Styles definition matching sample / credentialsLogin
font_tahoma_8_white_bold = Font(name='Tahoma', size=8, bold=True, color='FFFFFFFF')
font_tahoma_8_black_bold = Font(name='Tahoma', size=8, bold=True, color='FF000000')
font_tahoma_8_black_regular = Font(name='Tahoma', size=8, bold=False, color='FF000000')

fill_navy = PatternFill(fill_type='solid', start_color='FF000080', end_color='FF000080')
fill_white = PatternFill(fill_type='solid', start_color='FFFFFFFF', end_color='FFFFFFFF')
fill_orange = PatternFill(fill_type='solid', start_color='FFFF9900', end_color='FFFF9900')
fill_none = PatternFill(fill_type=None)

thin_border_side = Side(border_style='thin', color='FFB0B0B0')
thin_border_black = Side(border_style='thin', color='FF000000')
double_border_side = Side(border_style='double', color='FF000000')
medium_border_side = Side(border_style='medium', color='FF000000')

border_grid = Border(left=thin_border_side, right=thin_border_side, top=thin_border_side, bottom=thin_border_side)
border_col_a = Border(left=double_border_side, right=thin_border_black, top=None, bottom=None)
border_col_b = Border(left=thin_border_side, right=None, top=thin_border_side, bottom=thin_border_side)
border_col_d = Border(left=None, right=thin_border_side, top=thin_border_side, bottom=thin_border_side)
border_col_middle = Border(left=None, right=None, top=thin_border_side, bottom=thin_border_side)

align_center = Alignment(horizontal='center', vertical='center')
align_left_top = Alignment(horizontal='left', vertical='top')

# Vertical Alignments
align_utcid_vertical = Alignment(text_rotation=180, vertical='top', horizontal='center')
align_date_vertical = Alignment(text_rotation=255, vertical='top', horizontal='center')

def format_sheet(ws):
    max_r = ws.max_row
    # Find number of test cases from Row 7
    num_cases = 0
    for c in range(6, 40):
        val = ws.cell(7, c).value
        if val is not None and str(val).strip().startswith('UTCID'):
            num_cases += 1
        elif val is not None and str(val).strip() == '':
            pass
        else:
            if num_cases > 0:
                break
    
    if num_cases == 0:
        for c in range(6, 40):
            if any(ws.cell(r, c).value for r in range(8, max_r)):
                num_cases += 1
            else:
                break
    
    if num_cases == 0:
        num_cases = 1

    start_col = 6
    end_col = start_col + num_cases - 1
    start_col_letter = get_column_letter(start_col)
    end_col_letter = get_column_letter(end_col)

    # Format Row 7 (Header row)
    for c in range(1, 6):
        cell = ws.cell(7, c)
        cell.fill = fill_navy
        cell.font = font_tahoma_8_white_bold
        cell.alignment = align_center
        cell.border = Border(top=double_border_side, bottom=medium_border_side)

    ws.cell(7, 1).border = Border(left=double_border_side, top=double_border_side, bottom=medium_border_side)

    # Row 7 UTCID columns: Vertical Text Rotation 180
    for idx in range(num_cases):
        col = start_col + idx
        cell = ws.cell(7, col)
        if not cell.value or not str(cell.value).startswith('UTCID'):
            cell.value = f"UTCID{idx+1:02d}"
        cell.fill = fill_navy
        cell.font = font_tahoma_8_white_bold
        cell.alignment = align_utcid_vertical
        cell.border = Border(left=thin_border_black, right=thin_border_black, top=double_border_side, bottom=medium_border_side)

    # Clear beyond end_col on Row 7
    for col in range(end_col + 1, end_col + 10):
        cell = ws.cell(7, col)
        if cell.value is not None:
            cell.value = None
        cell.fill = fill_navy
        cell.font = font_tahoma_8_white_bold
        cell.border = Border(top=double_border_side, bottom=medium_border_side)

    # Find row indices for Result, Type, Passed/Failed, Executed Date
    type_row = None
    pf_row = None
    exec_row = None
    defect_row = None
    last_data_row = max_r

    for r in range(8, max_r + 1):
        val_b = str(ws.cell(r, 2).value).strip() if ws.cell(r, 2).value else ''
        if val_b == 'Type':
            type_row = r
        elif val_b in ['Passed/Failed', 'Passed / Failed', 'Pass/Fail']:
            pf_row = r
        elif val_b in ['Executed Date', 'Execute Date']:
            exec_row = r
        elif val_b in ['Defect ID', 'DefectId', 'Defect']:
            defect_row = r
            last_data_row = r

    if not type_row:
        type_row = max_r - 3
        pf_row = max_r - 2
        exec_row = max_r - 1
        defect_row = max_r
        last_data_row = max_r

    # Format Data Rows 8 to last_data_row
    for r in range(8, last_data_row + 1):
        c_a = ws.cell(r, 1)
        c_b = ws.cell(r, 2)
        c_c = ws.cell(r, 3)
        c_d = ws.cell(r, 4)
        c_e = ws.cell(r, 5)

        # Col A
        c_a.fill = fill_navy
        c_a.font = font_tahoma_8_white_bold
        c_a.alignment = align_center
        c_a.border = border_col_a

        # Col B, C, D
        val_b = str(c_b.value).strip() if c_b.value else ''
        is_section_header = val_b in [
            'Precondition', 'Command Data', 'Repository / Service', 'Expected Result',
            'Type', 'Passed/Failed', 'Executed Date', 'Defect ID',
            'Pre-condition', 'UserData', 'MockData', 'Confirm'
        ]

        c_b.fill = fill_white
        c_b.font = font_tahoma_8_black_bold if is_section_header else font_tahoma_8_black_regular
        c_b.alignment = align_left_top
        c_b.border = border_col_b

        c_c.fill = fill_white
        c_c.font = font_tahoma_8_black_regular
        c_c.border = border_col_middle

        c_d.fill = fill_white
        c_d.font = font_tahoma_8_black_regular
        c_d.border = border_col_d

        # Col E (Orange accent column)
        c_e.fill = fill_orange
        c_e.font = font_tahoma_8_black_regular
        c_e.border = Border(top=None, bottom=None, left=None, right=None)

        # Test Case cells (Col F to end_col)
        for col in range(start_col, end_col + 1):
            cell = ws.cell(r, col)
            cell.fill = fill_none  # Transparent / white
            cell.border = border_grid

            if r == type_row:
                cell.font = font_tahoma_8_black_bold
                cell.alignment = align_center
            elif r == pf_row:
                cell.font = font_tahoma_8_black_bold
                cell.alignment = align_center
            elif r == exec_row:
                cell.font = font_tahoma_8_black_regular
                cell.alignment = align_date_vertical  # Vertical Text Rotation 255
                cell.number_format = 'm"/"d"/"yyyy'
            else:
                cell.font = font_tahoma_8_black_regular
                cell.alignment = align_center

    # Bottom border on last row
    for col in range(1, end_col + 1):
        cell = ws.cell(last_data_row, col)
        curr_b = cell.border
        cell.border = Border(
            left=curr_b.left,
            right=curr_b.right,
            top=curr_b.top,
            bottom=medium_border_side
        )

    # Set formulas in Row 5
    ws['A5'] = f'=COUNTIF({start_col_letter}{pf_row}:{end_col_letter}{pf_row},"P")'
    ws['C5'] = f'=COUNTIF({start_col_letter}{pf_row}:{end_col_letter}{pf_row},"F")'
    ws['F5'] = f'=COUNTIF({start_col_letter}{pf_row}:{end_col_letter}{pf_row},"U")'
    ws['L5'] = f'=COUNTIF({start_col_letter}{type_row}:{end_col_letter}{type_row},"N")'
    ws['M5'] = f'=COUNTIF({start_col_letter}{type_row}:{end_col_letter}{type_row},"A")'
    ws['N5'] = f'=COUNTIF({start_col_letter}{type_row}:{end_col_letter}{type_row},"B")'
    ws['O5'] = f'=COUNTA({start_col_letter}7:{end_col_letter}7)'

    # Data validation
    ws.data_validations.dataValidation.clear()

    dv_o = DataValidation(type="list", formula1='"O"', allow_blank=True)
    ws.add_data_validation(dv_o)
    dv_o.add(f'{start_col_letter}8:{end_col_letter}{type_row - 1}')

    dv_type = DataValidation(type="list", formula1='"N,A,B"', allow_blank=True)
    ws.add_data_validation(dv_type)
    dv_type.add(f'{start_col_letter}{type_row}:{end_col_letter}{type_row}')

    dv_pf = DataValidation(type="list", formula1='"P,F,U"', allow_blank=True)
    ws.add_data_validation(dv_pf)
    dv_pf.add(f'{start_col_letter}{pf_row}:{end_col_letter}{pf_row}')

def fix_hyperlinks_and_statistics(wb):
    ml = wb['MethodList']
    stats = wb['Statistics']

    for r in range(11, 89):
        no = ml.cell(r, 1).value
        mname = ml.cell(r, 3).value
        sheet_name = ml.cell(r, 4).value

        if sheet_name and isinstance(sheet_name, str) and '#' in sheet_name:
            import re
            m = re.search(r'#([A-Za-z0-9_]+)!', sheet_name)
            if m:
                sheet_name = m.group(1)

        if not sheet_name or sheet_name not in wb.sheetnames:
            if mname and mname in wb.sheetnames:
                sheet_name = mname

        if sheet_name and sheet_name in wb.sheetnames:
            ml.cell(r, 4, f'=HYPERLINK("#{sheet_name}!A1", "{sheet_name}")')
            stats.cell(r, 1, no)
            stats.cell(r, 2, f'=HYPERLINK("#{sheet_name}!A1", MethodList!C{r})')
            stats.cell(r, 3, f'={sheet_name}!A5')
            stats.cell(r, 4, f'={sheet_name}!C5')
            stats.cell(r, 5, f'={sheet_name}!F5')
            stats.cell(r, 6, f'={sheet_name}!L5')
            stats.cell(r, 7, f'={sheet_name}!M5')
            stats.cell(r, 8, f'={sheet_name}!N5')
            stats.cell(r, 9, f'=SUM(C{r}:H{r})')

def main():
    wb = openpyxl.load_workbook(EXCEL_PATH)
    
    # Fix links for all rows in MethodList & Statistics
    fix_hyperlinks_and_statistics(wb)

    # Format all test sheets from getAwsCostChartData onwards
    target_sheets = [
        'getAwsCostSummary',
        'getAwsCostChartData',
        'getAzureCostSummary',
        'getAzureCostChartData',
        'getOpenAiCostSummary',
        'getOpenAiCostChartData',
        'syncAzureCostFullBackfill',
        'syncAzureCostIncremental',
        'syncAzureCostCustomRange',
        'domainToResultAwsCost',
        'countUnreadByUserId',
        'getListByUserId',
        'markAllAsRead',
        'markAsRead',
        'domainToResultNotification',
        'adminUpgradeSubscription',
        'cancelPayment',
        'confirmPayment',
        'createPayment',
        'getPaymentByOrderCode',
        'getPaymentsByUserId',
        'getAllPaymentOrders',
        'createPersona',
        'getAllPersonas',
        'getPersonaById',
        'getConversationStyleByPersonaId',
        'updatePersona',
        'getRandomQuote',
        'importQuote',
        'findTodayUserDailyAiUsage',
        'getUserActiveSubscriptionPlan',
        'getUserActiveSubscription',
        'listActivePlans',
        'updateSubscriptionPlan'
    ]

    for s_name in target_sheets:
        if s_name in wb.sheetnames:
            print(f'Formatting sheet: {s_name}')
            format_sheet(wb[s_name])
        else:
            print(f'Warning: sheet {s_name} not found!')

    wb.save(EXCEL_PATH)
    print('Successfully applied vertical headers and date alignments to all sheets!')

if __name__ == '__main__':
    main()
