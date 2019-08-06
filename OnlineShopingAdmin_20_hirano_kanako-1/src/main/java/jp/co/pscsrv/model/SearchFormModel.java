package jp.co.pscsrv.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;

public class SearchFormModel {
	@Pattern(regexp = "^[0-9]*$", message = "「会員Noは必ず半角数字で入力してください。」")
	private String memberNo;
	private String memberName;
	private String startYear;
	private String startMonth;
	private String startDay;
	private String endYear;
	private String endMonth;
	private String endDay;

	@Pattern(regexp = "^[0-9]*$", message = "「合計金額上限は必ず半角数字で入力してください。」")
	private String priceMax;

	@Pattern(regexp = "^[0-9]*$", message = "「合計金額下限は半角数字で入力してください。」")
	private String priceMin;

	private boolean positivePriceMax;
	private boolean positivePriceMin;
	private boolean priceIntegrity;
	private boolean dateIntegrity;
	private boolean startDateExist;
	private boolean endDateExist;

	@AssertTrue(message = "「合計金額上限には正の数で入力してください。」")
	private boolean isPositivePriceMax() {
		if (priceMax.equals("") || priceMax == null) {
			return true;
		}
		try {
			if (Integer.parseInt(priceMax) > 0) {
				return true;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}

	@AssertTrue(message = "「合計金額下限には正の数で入力してください。」")
	private boolean isPositivePriceMin() {
		if (priceMin.equals("") || priceMin == null) {
			return true;
		}
		try {
			if (Integer.parseInt(priceMin) > 0) {
				return true;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}

	@AssertTrue(message = "「合計金額上限は合計金額下限より大きい値を入力してください。」")
	private boolean isPriceIntegrity() {

		if (priceMax.equals("") || priceMin.equals("") || priceMax == null || priceMin == null) {
			return true;
		}

		if (Integer.parseInt(priceMax) > Integer.parseInt(priceMin)) {
			return true;
		}
		return false;
	}

	@AssertTrue(message = "「注文日(開始年月日)には存在する日付を入力してください。」")
	private boolean isStartDateExist() {
		if(startYear.equals("") && startMonth.equals("") && startDay.equals("")) {
			return true;
		}

		try {
			String sY = String.format("%02d", Integer.parseInt(startYear));
			String sM = String.format("%02d", Integer.parseInt(startMonth));
			String sD = String.format("%02d", Integer.parseInt(startDay));
			// 変換対象の日付文字列
			String dateStrat = sY + sM + sD;

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sdf.setLenient(false);

			// Date型変換
			Date formatDateStart = sdf.parse(dateStrat);

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@AssertTrue(message = "「注文日(終了年月日)には存在する日付を入力してください。」")
	private boolean isEndDateExist() {
		if(endYear.equals("") && endMonth.equals("") && endDay.equals("")) {
			return true;
		}

		try {
			String eY = String.format("%02d", Integer.parseInt(endYear));
			String eM = String.format("%02d", Integer.parseInt(endMonth));
			String eD = String.format("%02d", Integer.parseInt(endDay));

			// 変換対象の日付文字列
			String dateEnd = eY + eM + eD;

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sdf.setLenient(false);

			// Date型変換
			Date formatDateEnd = sdf.parse(dateEnd);

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@AssertTrue(message = "「注文日(開始年月日)は注文日(終了年月日)より未来の日付を入力してください。」")
	private boolean isDateIntegrity() {
		if(startYear.equals("") && startMonth.equals("") && startDay.equals("")) {
			return true;
		}

		if(endYear.equals("") && endMonth.equals("") && endDay.equals("")) {
			return true;
		}

		try {
			// 変換対象の日付文字列
			String dateStrat = startYear + startMonth + startDay;
			String dateEnd = endYear + endMonth + endDay;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMd");
			// Date型変換
			Date formatDateStart = sdf.parse(dateStrat);
			Date formatDateEnd = sdf.parse(dateEnd);

			//開始日が終了日よりも過去でないならば
			if (formatDateStart.before(formatDateEnd)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public String getMemberNo() {
		return memberNo;
	}

	public void setMemberNo(String memberNo) {
		this.memberNo = memberNo;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getStartDay() {
		return startDay;
	}

	public void setStartDay(String startDay) {
		this.startDay = startDay;
	}

	public String getEndYear() {
		return endYear;
	}

	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

	public String getEndDay() {
		return endDay;
	}

	public void setEndDay(String endDay) {
		this.endDay = endDay;
	}

	public String getPriceMax() {
		return priceMax;
	}

	public void setPriceMax(String priceMax) {
		this.priceMax = priceMax;
	}

	public String getPriceMin() {
		return priceMin;
	}

	public void setPriceMin(String priceMin) {
		this.priceMin = priceMin;
	}

	public void setPositivePriceMax(boolean positivePriceMax) {
		this.positivePriceMax = positivePriceMax;
	}

	public boolean getPositivePriceMax() {
		return positivePriceMax;
	}

	public void setPositivePriceMin(boolean positivePriceMin) {
		this.positivePriceMin = positivePriceMin;
	}

	public boolean getPositivePriceMin() {
		return positivePriceMin;
	}

	public void setPriceIntegrity(boolean priceIntegrity) {
		this.priceIntegrity = priceIntegrity;
	}

	public boolean getPriceIntegrity() {
		return priceIntegrity;
	}

	public void setDateIntegrity(boolean dateIntegrity) {
		this.dateIntegrity = dateIntegrity;
	}

	public boolean getDateIntegrity() {
		return dateIntegrity;
	}

	public void setStartDateExist(boolean startDateExist) {
		this.startDateExist = startDateExist;
	}

	public boolean getStartDateExist() {
		return startDateExist;
	}

	public void setEndDateExist(boolean endDateExist) {
		this.endDateExist = endDateExist;
	}

	public boolean getEndDateExist() {
		return endDateExist;
	}
}
