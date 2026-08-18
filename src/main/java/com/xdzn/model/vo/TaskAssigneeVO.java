package com.xdzn.model.vo;

import lombok.Data;

/**
 * TaskAssigneeVO
 * <p>
 * 任务指派成员视图对象，返回成员的基础信息（id / 姓名 / 头像）。
 *
 * @author xdzn
 */
@Data
public class TaskAssigneeVO {

    /**
     * 被指派成员 id
     */
    private Long memberId;

    /**
     * 成员姓名
     */
    private String memberName;

    /**
     * 成员头像地址
     */
    private String memberAvatar;
}
