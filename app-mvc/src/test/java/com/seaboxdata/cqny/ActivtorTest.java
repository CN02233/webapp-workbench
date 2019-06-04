package com.seaboxdata.cqny;

import com.AbstractTestService;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.Options;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ActivtorTest extends AbstractTestService {

    @Test
    public void test(){
        AviatorEvaluator.setOption(Options.ALWAYS_PARSE_FLOATING_POINT_NUMBER_INTO_DECIMAL, true);
        Map<String,Object> params= new HashMap<>();
        params.put("FL242_2281FL",new BigDecimal(20));
        params.put("FL242_2287FL",new BigDecimal(30));
        params.put("FL242_2279FL",new BigDecimal(10));
        Expression expression= AviatorEvaluator.compile("FL242_2281FL>=FL242_2287FL?FL242_2279FL:(FL242_2279FL/FL242_2287FL)");
        Object result = expression.execute(params);
        System.out.println(result);
    }
}
