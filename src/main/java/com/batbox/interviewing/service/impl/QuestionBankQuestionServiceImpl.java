package com.batbox.interviewing.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.batbox.interviewing.common.ErrorCode;
import com.batbox.interviewing.constant.CommonConstant;
import com.batbox.interviewing.exception.ThrowUtils;
import com.batbox.interviewing.mapper.QuestionBankQuestionMapper;
import com.batbox.interviewing.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.batbox.interviewing.model.entity.QuestionBankQuestion;
import com.batbox.interviewing.model.entity.User;
import com.batbox.interviewing.model.vo.QuestionBankQuestionVO;
import com.batbox.interviewing.model.vo.UserVO;
import com.batbox.interviewing.service.QuestionBankQuestionService;
import com.batbox.interviewing.service.UserService;
import com.batbox.interviewing.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题库题库关联服务实现
 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add                  对创建的数据进行校验
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion,
                                          boolean add) {
        ThrowUtils.throwIf(questionBankQuestion == null,
                           ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        // 创建数据时，参数不能为空
        //        if (add) {
        //            // todo 补充校验规则
        //        }
        // 修改数据时，有参数则校验
        //        if (StringUtils.isNotBlank(title)) {
        //            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        //        }
    }

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankQuestionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionBankQuestionQueryRequest.getId();
        Long notId = questionBankQuestionQueryRequest.getNotId();
        String sortField = questionBankQuestionQueryRequest.getSortField();
        String sortOrder = questionBankQuestionQueryRequest.getSortOrder();
        Long userId = questionBankQuestionQueryRequest.getUserId();
        Long questionBankId = questionBankQuestionQueryRequest.getQuestionBankId();
        Long questionId = questionBankQuestionQueryRequest.getQuestionId();

        // todo 补充需要的查询条件
        // 从多字段中搜索

        // 模糊查询

        // JSON 数组查询

        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId),
                        "id",
                        notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id),
                        "id",
                        id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId),
                        "userId",
                        userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId),
                        "questionId",
                        questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionBankId),
                        "questionBankId",
                        questionBankId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                             sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                             sortField);
        return queryWrapper;
    }

    /**
     * 获取题库题库关联封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion,
                                                            HttpServletRequest request) {
        // 对象转封装类
        QuestionBankQuestionVO questionBankQuestionVO = QuestionBankQuestionVO.objToVo(questionBankQuestion);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = questionBankQuestion.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankQuestionVO.setUser(userVO);
        // endregion

        return questionBankQuestionVO;
    }

    /**
     * 分页获取题库题库关联封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage,
                                                                      HttpServletRequest request) {
        List<QuestionBankQuestion> questionBankQuestionList = questionBankQuestionPage.getRecords();
        Page<QuestionBankQuestionVO> questionBankQuestionVOPage = new Page<>(questionBankQuestionPage.getCurrent(),
                                                                             questionBankQuestionPage.getSize(),
                                                                             questionBankQuestionPage.getTotal());
        if (CollUtil.isEmpty(questionBankQuestionList)) {
            return questionBankQuestionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankQuestionVO> questionBankQuestionVOList = questionBankQuestionList.stream()
                                                                                          .map(questionBankQuestion -> {
                                                                                              return QuestionBankQuestionVO.objToVo(questionBankQuestion);
                                                                                          })
                                                                                          .collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionBankQuestionList.stream()
                                                      .map(QuestionBankQuestion::getUserId)
                                                      .collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet)
                                                             .stream()
                                                             .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        questionBankQuestionVOList.forEach(questionBankQuestionVO -> {
            Long userId = questionBankQuestionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId)
                                        .get(0);
            }
            questionBankQuestionVO.setUser(userService.getUserVO(user));
        });
        // endregion

        questionBankQuestionVOPage.setRecords(questionBankQuestionVOList);
        return questionBankQuestionVOPage;
    }

}
