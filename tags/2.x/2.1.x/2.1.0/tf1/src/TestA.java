import static org.junit.Assert.*;

import org.junit.Test;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;

public class TestA {

	@Test
	public void test() throws CloneNotSupportedException {
		CategoryDTO categoryDTO = new CategoryDTO("channel", "name", "identifier", "extension");
		CategoryDTO categoryDTO2 = (CategoryDTO) categoryDTO.clone();
		assertEquals(categoryDTO.getChannel(), categoryDTO2.getChannel());
	}

}
