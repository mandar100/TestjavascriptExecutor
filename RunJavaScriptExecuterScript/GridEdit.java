package frames;

public class GridEdit {
	public static void main(String[] args) {


		//tbody[@class='gdLeftTableDimensionValues']/tr/td[@level='0']/div[@title='Internet MMDA']/ancestor::tr[1]/td[@level='1']/div[@title='A: [0k-1k[']
		
		//tbody[@class='gdLeftTableDimensionValues']//div[@title='Internet MMDA']/ancestor::tr[1]//div[@title='A: [0k-1k[']
		
		// prowkey="6000/14000/4000"
		
		//td[@prowkey='6000/14000/4000' and @class='']

		
		String Dim1_Val1="6000";
		String Dim2_Val2="14000";

		
		String sDimension = "Dim1_Val1, Dim2_Val2";
		String[] sDimensions = sDimension.split(",");
		
		
		
	}

}
