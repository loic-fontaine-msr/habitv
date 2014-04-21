package ted.tools;

import java.util.Vector;

import ted.TedConfig;
import ted.TedSerie;

public class Sorter 
{	
	//Vector<TedSerie> sortVector;
	
	public Sorter()
	{
		
	}

	public void sortTable(Vector<TedSerie> tableData) 
	{
		// iterate through the list and sort it based on the sortType	
		this.quicksort(tableData, 0, tableData.size()-1);
	}
		
	// quicksort a[left] to a[right]
    public void quicksort(Vector<TedSerie> tableData, int left, int right) 
    {
        if (right <= left) return;
        int i = partition(tableData, left, right);
        quicksort(tableData, left, i-1);
        quicksort(tableData, i+1, right);
    }

    // partition a[left] to a[right], assumes left < right
    private int partition(Vector<TedSerie> tableData, int left, int right) 
    {
        int i = left - 1;
        int j = right;
        while (true) 
        {
            while (isSmaller(tableData, ++i, right))      // find item on left to swap
                if (i == right) break;                              // a[right] acts as sentinel
            while (isSmaller(tableData, right, --j))     // find item on right to swap
                if (j == left) break;           // don't go out-of-bounds
            if (i >= j) break;                  // check if pointers cross
            swap(tableData, i, j);                      // swap two elements into place
        }
        swap(tableData, i, right);                      // swap with partition element
        return i;
    }

	/**
	 * @param tableData 
	 * @param a
	 * @param b
	 * @return if a is smaller than b.
	 */
	private boolean isSmaller(Vector<TedSerie> tableData, int a, int b) 
	{
		TedSerie serieA = tableData.elementAt(a);
		TedSerie serieB = tableData.elementAt(b);
		
		boolean smaller = (serieA.compareTo(serieB) < 0);
		
		// when sorting descending, invert the result
		if (TedConfig.getInstance().getSortDirection() == TedConfig.SORT_DESCENDING)
		{
			smaller = !smaller;
		}
		
		return smaller;
	}

	private void swap(Vector<TedSerie> tableData, int a, int b) 
	{
		TedSerie temp = tableData.elementAt(a);
		tableData.setElementAt(tableData.elementAt(b), a);
		tableData.setElementAt(temp, b);
	}

}
