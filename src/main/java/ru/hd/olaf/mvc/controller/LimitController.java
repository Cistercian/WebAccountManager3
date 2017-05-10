package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.hd.olaf.mvc.service.AmountService;
import ru.hd.olaf.mvc.service.LimitService;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.json.BarEntity;
import ru.hd.olaf.util.json.JsonResponse;

/**
 * Created by d.v.hozyashev on 10.05.2017.
 */
@Controller
public class LimitController {

    @Autowired
    private LimitService limitService;
    @Autowired
    private AmountService amountService;

    private static final Logger logger = LoggerFactory.getLogger(LimitController.class);

    @RequestMapping(value = "/statistic/limit", method = RequestMethod.GET)
    public @ResponseBody JsonResponse getViewLimit(){
        logger.debug(LogUtil.getMethodName());
        for (BarEntity entity : limitService.getLimit()){
            logger.debug(entity.toString());
        }
        return null;
    }
}
