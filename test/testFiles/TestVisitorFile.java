package testFiles;

public class TestVisitorFile
{
	private interface Interface1{
	}
	
	private interface Interface2{
	}
	
	private interface Interface3{
	}
	
	public interface GroupedInterface extends Interface1, Interface2, Interface3{
	}
	
	public class testVisitorFile_1 extends TestVisitorFile  implements GroupedInterface, Interface1
	{
		public class testVisitorFile_2 extends TestVisitorFile implements Interface2
		{
		}
	}
	
	public class testVisitorFile_3 implements Interface3
	{
		public class testVisitorFile_3_1 extends testVisitorFile_3 implements Interface1, Interface3
		{
		}
		
		public class testVisitorFile_3_2 extends testVisitorFile_3 implements GroupedInterface
		{
		}
	}
	

}
