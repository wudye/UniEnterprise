package com.fhs.trans.service;

import com.fhs.core.trans.vo.VO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link AutoTransable} 接口的单元测试
 *
 * @author your-name
 */
public class AutoTransableTest extends BaseMockitoUnitTest {

    /**
     * 测试用的 VO 实现类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    private static class TestVO implements VO {
        private static final long serialVersionUID = 1L;

        private Long id;
        private String name;
        private String code;
    }

    /**
     * 最小实现的 AutoTransable（只实现 selectById）
     */
    private AutoTransable<TestVO> minimalService;

    /**
     * 完整实现的 AutoTransable（覆盖所有方法）
     */
    private AutoTransable<TestVO> fullService;

    private TestVO testVO1;
    private TestVO testVO2;
    private TestVO testVO3;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testVO1 = new TestVO(1L, "Test1", "CODE_001");
        testVO2 = new TestVO(2L, "Test2", "CODE_002");
        testVO3 = new TestVO(3L, "Test3", "CODE_003");

        // 最小实现：只实现 selectById
        minimalService = new AutoTransable<TestVO>() {
            @Override
            public TestVO selectById(Object primaryValue) {
                if (primaryValue == null) {
                    return null;
                }
                Long id = Long.valueOf(primaryValue.toString());
                if (id.equals(1L)) {
                    return testVO1;
                } else if (id.equals(2L)) {
                    return testVO2;
                }
                return null;
            }
        };

        // 完整实现：覆盖所有方法
        fullService = new AutoTransable<TestVO>() {
            @Override
            public TestVO selectById(Object primaryValue) {
                if (primaryValue == null) {
                    return null;
                }
                Long id = Long.valueOf(primaryValue.toString());
                if (id.equals(1L)) {
                    return testVO1;
                } else if (id.equals(2L)) {
                    return testVO2;
                } else if (id.equals(3L)) {
                    return testVO3;
                }
                return null;
            }

            @SuppressWarnings("deprecation")
            @Override
            public List<TestVO> findByIds(List<? extends Object> ids) {
                List<TestVO> result = new ArrayList<>();
                for (Object id : ids) {
                    TestVO vo = selectById(id);
                    if (vo != null) {
                        result.add(vo);
                    }
                }
                return result;
            }

            @Override
            public List<TestVO> select() {
                return Arrays.asList(testVO1, testVO2, testVO3);
            }
        };
    }

    // ==================== selectById 测试 ====================

    @Test
    void testSelectById_withExistingId_shouldReturnVO() {
        // 调用
        TestVO result = minimalService.selectById(1L);

        // 断言
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test1", result.getName());
        assertEquals("CODE_001", result.getCode());
    }

    @Test
    void testSelectById_withStringId_shouldReturnVO() {
        // 调用（传入字符串类型的 ID）
        TestVO result = minimalService.selectById("2");

        // 断言
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void testSelectById_withNonExistingId_shouldReturnNull() {
        // 调用
        TestVO result = minimalService.selectById(999L);

        // 断言
        assertNull(result);
    }

    @Test
    void testSelectById_withNullId_shouldHandleGracefully() {
        // 调用
        TestVO result = minimalService.selectById(null);

        // 断言
        assertNull(result);
    }

    // ==================== selectByIds 测试 ====================

    @Test
    void testSelectByIds_withValidIds_shouldReturnList() {
        // 准备参数
        List<Long> ids = Arrays.asList(1L, 2L);

        // 调用
        List<TestVO> result = fullService.selectByIds(ids);

        // 断言
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testVO1));
        assertTrue(result.contains(testVO2));
    }

    @Test
    void testSelectByIds_withPartialExistingIds_shouldReturnExistingOnly() {
        // 准备参数
        List<Long> ids = Arrays.asList(1L, 999L);

        // 调用
        List<TestVO> result = fullService.selectByIds(ids);

        // 断言
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testVO1, result.get(0));
    }

    @Test
    void testSelectByIds_withEmptyList_shouldReturnEmptyList() {
        // 准备参数
        List<Long> ids = new ArrayList<>();

        // 调用
        List<TestVO> result = fullService.selectByIds(ids);

        // 断言
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSelectByIds_withNullList_shouldHandleGracefully() {
        // 调用
        List<TestVO> result = fullService.selectByIds(null);

        // 断言（取决于实现，这里测试的是不会抛出异常）
        assertNotNull(result);
    }

    @Test
    void testSelectByIds_defaultImplementation_shouldDelegateToFindByIds() {
        // 准备参数
        List<Long> ids = Arrays.asList(1L, 2L);

        // 调用（使用最小实现，测试默认行为）
        @SuppressWarnings("deprecation")
        List<TestVO> result = minimalService.selectByIds(ids);

        // 断言（默认实现返回空列表）
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== findByIds (已废弃) 测试 ====================

    @SuppressWarnings("deprecation")
    @Test
    void testFindByIds_defaultImplementation_shouldReturnEmptyList() {
        // 准备参数
        List<Long> ids = Arrays.asList(1L, 2L);

        // 调用（使用最小实现）
        List<TestVO> result = minimalService.findByIds(ids);

        // 断言
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @SuppressWarnings("deprecation")
    @Test
    void testFindByIds_customImplementation_shouldReturnData() {
        // 准备参数
        List<Long> ids = Arrays.asList(1L, 2L);

        // 调用（使用完整实现）
        List<TestVO> result = fullService.findByIds(ids);

        // 断言
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ==================== select (全量查询) 测试 ====================

    @Test
    void testSelect_defaultImplementation_shouldReturnEmptyList() {
        // 调用（使用最小实现）
        List<TestVO> result = minimalService.select();

        // 断言
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSelect_customImplementation_shouldReturnAllData() {
        // 调用（使用完整实现）
        List<TestVO> result = fullService.select();

        // 断言
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(testVO1));
        assertTrue(result.contains(testVO2));
        assertTrue(result.contains(testVO3));
    }

    // ==================== 边界条件测试 ====================

    @Test
    void testSelectByIds_withMixedTypes_shouldHandleCorrectly() {
        // 准备参数（混合类型 ID）
        List<Object> ids = Arrays.asList(1L, "2", 3);

        // 调用
        List<TestVO> result = fullService.selectByIds(ids);

        // 断言
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testInterfaceContract_selectByIdIsAbstract() {
        // 验证 selectById 是抽象方法（通过编译检查）
        // 如果尝试 new AutoTransable<>() { } 不实现 selectById 会编译错误

        // 验证默认方法存在且可调用
        assertDoesNotThrow(() -> {
            minimalService.selectByIds(Arrays.asList(1L));
            minimalService.select();
        });
    }
}
