use intelliedu;

INSERT INTO application (id, app_name, description, image_url, type, strategy, audit_status, audit_message, auditor_id,
                         audit_time, user_id, create_time, update_time, deleted)
VALUES (1, '自定义MBTI性格测试', '测试性格', '11', 1, 0, 1, null, null, null, 1, '2024-04-24 15:58:05',
        '2024-05-09 15:09:53', 0);

INSERT INTO application (id, app_name, description, image_url, type, strategy, audit_status, audit_message, auditor_id,
                         audit_time, user_id, create_time, update_time, deleted)
VALUES (2, '自定义得分测试', '测试得分', '22', 0, 0, 1, null, null, null, 1, '2024-04-25 11:39:30',
        '2024-05-09 15:09:53', 0);

INSERT INTO application (id, app_name, description, image_url, type, strategy, audit_status, audit_message, auditor_id,
                         audit_time, user_id, create_time, update_time, deleted)
VALUES (3, 'AI MBTI 性格测试', '快来测测你的 MBTI', '11', 1, 1, 1, null, null, null, 1, '2024-04-26 16:38:12',
        '2024-05-09 15:09:53', 0);

INSERT INTO application (id, app_name, description, image_url, type, strategy, audit_status, audit_message, auditor_id,
                         audit_time, user_id, create_time, update_time, deleted)
VALUES (4, 'AI 得分测试', '看看你熟悉多少首都', '22', 0, 1, 1, null, null, null, 1, '2024-04-26 16:38:56',
        '2024-05-09 15:09:53', 0);


INSERT INTO question (id, questions, app_id, user_id, create_time, update_time, deleted)
VALUES (1,
        '[{"options":[{"evaluation":"I","value":"独自工作","key":"A","grade":0},{"evaluation":"E","value":"与他人合作","key":"B","grade":0}],"title":"1. 你通常更喜欢"},
          {"options":[{"evaluation":"J","value":"喜欢有明确的计划","key":"A","grade":0},{"evaluation":"P","value":"更愿意随机应变","key":"B","grade":0}],"title":"2. 当安排活动时"},
          {"options":[{"evaluation":"T","value":"认为应该严格遵守","key":"A","grade":0},{"evaluation":"F","value":"认为应灵活运用","key":"B","grade":0}],"title":"3. 你如何看待规则"},
          {"options":[{"evaluation":"E","value":"经常是说话的人","key":"A","grade":0},{"evaluation":"I","value":"更倾向于倾听","key":"B","grade":0}],"title":"4. 在社交场合中"},
          {"options":[{"evaluation":"J","value":"先研究再行动","key":"A","grade":0},{"evaluation":"P","value":"边做边学习","key":"B","grade":0}],"title":"5. 面对新的挑战"},
          {"options":[{"evaluation":"S","value":"注重细节和事实","key":"A","grade":0},{"evaluation":"N","value":"注重概念和想象","key":"B","grade":0}],"title":"6. 在日常生活中"},
          {"options":[{"evaluation":"T","value":"更多基于逻辑分析","key":"A","grade":0},{"evaluation":"F","value":"更多基于个人情感","key":"B","grade":0}],"title":"7. 做决定时"},
          {"options":[{"evaluation":"S","value":"喜欢有结构和常规","key":"A","grade":0},{"evaluation":"N","value":"喜欢自由和灵活性","key":"B","grade":0}],"title":"8. 对于日常安排"},
          {"options":[{"evaluation":"P","value":"首先考虑可能性","key":"A","grade":0},{"evaluation":"J","value":"首先考虑后果","key":"B","grade":0}],"title":"9. 当遇到问题时"},
          {"options":[{"evaluation":"T","value":"时间是一种宝贵的资源","key":"A","grade":0},{"evaluation":"F","value":"时间是相对灵活的概念","key":"B","grade":0}],"title":"10. 你如何看待时间"}]',
        1, 1, '2024-04-24 16:41:53', '2024-05-09 12:28:58', 0);


INSERT INTO question (id, questions, app_id, user_id, create_time, update_time, deleted)
VALUES (2,
        '[{"options":[{"grade":0,"value":"利马","key":"A","evaluation":""},{"grade":0,"value":"圣多明各","key":"B","evaluation":""},{"grade":0,"value":"圣萨尔瓦多","key":"C","evaluation":""},{"grade":1,"value":"波哥大","key":"D","evaluation":""}],"title":"哥伦比亚的首都是?"},
          {"options":[{"grade":0,"value":"蒙特利尔","key":"A","evaluation":""},{"grade":0,"value":"多伦多","key":"B","evaluation":""},{"grade":1,"value":"渥太华","key":"C","evaluation":""},{"grade":0,"value":"温哥华","key":"D","evaluation":""}],"title":"加拿大的首都是?"},
          {"options":[{"grade":0,"value":"大阪","key":"A","evaluation":""},{"grade":1,"value":"东京","key":"B","evaluation":""},{"grade":0,"value":"京都","key":"C","evaluation":""},{"grade":0,"value":"名古屋","key":"D","evaluation":""}],"title":"日本的首都是?"},
          {"options":[{"grade":0,"value":"墨尔本","key":"A","evaluation":""},{"grade":0,"value":"悉尼","key":"B","evaluation":""},{"grade":0,"value":"布里斯班","key":"C","evaluation":""},{"grade":1,"value":"堪培拉","key":"D","evaluation":""}],"title":"澳大利亚的首都是?"},
          {"options":[{"grade":1,"value":"雅加达","key":"A","evaluation":""},{"grade":0,"value":"曼谷","key":"B","evaluation":""},{"grade":0,"value":"胡志明市","key":"C","evaluation":""},{"grade":0,"value":"吉隆坡","key":"D","evaluation":""}],"title":"印度尼西亚的首都是?"},
          {"options":[{"grade":0,"value":"上海","key":"A","evaluation":""},{"grade":0,"value":"杭州","key":"B","evaluation":""},{"grade":1,"value":"北京","key":"C","evaluation":""},{"grade":0,"value":"广州","key":"D","evaluation":""}],"title":"中国的首都是?"},
          {"options":[{"grade":0,"value":"汉堡","key":"A","evaluation":""},{"grade":0,"value":"慕尼黑","key":"B","evaluation":""},{"grade":1,"value":"柏林","key":"C","evaluation":""},{"grade":0,"value":"科隆","key":"D","evaluation":""}],"title":"德国的首都是?"},
          {"options":[{"grade":0,"value":"釜山","key":"A","evaluation":""},{"grade":1,"value":"首尔","key":"B","evaluation":""},{"grade":0,"value":"大田","key":"C","evaluation":""},{"grade":0,"value":"仁川","key":"D","evaluation":""}],"title":"韩国的首都是?"},
          {"options":[{"grade":0,"value":"瓜达拉哈拉","key":"A","evaluation":""},{"grade":0,"value":"蒙特雷","key":"B","evaluation":""},{"grade":1,"value":"墨西哥城","key":"C","evaluation":""},{"grade":0,"value":"坎昆","key":"D","evaluation":""}],"title":"墨西哥的首都是?"},
          {"options":[{"grade":1,"value":"开罗","key":"A","evaluation":""},{"grade":0,"value":"亚历山大","key":"B","evaluation":""},{"grade":0,"value":"卢克索","key":"C","evaluation":""},{"grade":0,"value":"卡利乌比亚","key":"D","evaluation":""}],"title":"埃及的首都是?"}]',
        2, 1, '2024-04-25 15:03:07', '2024-05-09 12:28:58', 0);


INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (1, 'ISTJ（物流师）', '忠诚可靠，被公认为务实，注重细节。', 'icon_url_istj', '["I","S","T","J"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (2, 'ISFJ（守护者）', '善良贴心，以同情心和责任为特点。', 'icon_url_isfj', '["I","S","F","J"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (3, 'INFJ（占有者）', '理想主义者，有着深刻的洞察力，善于理解他人。', 'icon_url_infj', '["I","N","F","J"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (4, 'INTJ（设计师）', '独立思考者，善于规划和实现目标，理性而果断。', 'icon_url_intj', '["I","N","T","J"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (5, 'ISTP（运动员）', '冷静自持，善于解决问题，擅长实践技能。', 'icon_url_istp', '["I","S","T","P"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (6, 'ISFP（艺术家）', '具有艺术感和敏感性，珍视个人空间和自由。', 'icon_url_isfp', '["I","S","F","P"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (7, 'INFP（治愈者）', '理想主义者，富有创造力，以同情心和理解他人著称。', 'icon_url_infp', '["I","N","F","P"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (8, 'INTP（学者）', '思维清晰，探索精神，独立思考且理性。', 'icon_url_intp', '["I","N","T","P"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (9, 'ESTP（拓荒者）', '敢于冒险，乐于冒险，思维敏捷，行动果断。', 'icon_url_estp', '["E","S","T","P"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (10, 'ESFP（表演者）', '热情开朗，善于社交，热爱生活，乐于助人。', 'icon_url_esfp', '["E","S","F","P"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (11, 'ENFP（倡导者）', '富有想象力，充满热情，善于激发他人的活力和潜力。', 'icon_url_enfp', '["E","N","F","P"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (12, 'ENTP（发明家）', '充满创造力，善于辩论，挑战传统，喜欢探索新领域。', 'icon_url_entp', '["E","N","T","P"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (13, 'ESTJ（主管）', '务实果断，善于组织和管理，重视效率和目标。', 'icon_url_estj', '["E","S","T","J"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (14, 'ESFJ（尽责者）', '友善热心，以协调、耐心和关怀为特点，善于团队合作。', 'icon_url_esfj', '["E","S","F","J"]',
        null, '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (15, 'ENFJ（教导者）', '热情关爱，善于帮助他人，具有领导力和社交能力。', 'icon_url_enfj', '["E","N","F","J"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (16, 'ENTJ（统帅）', '果断自信，具有领导才能，善于规划和执行目标。', 'icon_url_entj', '["E","N","T","J"]', null,
        '2024-04-24 16:57:02', '2024-05-09 12:28:21', 0, 1, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (17, '首都知识大师', '你真棒棒哦，首都知识非常出色！', null, null, 9, '2024-04-25 15:05:44', '2024-05-09 12:28:21',
        0, 2, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (18, '地理小能手！', '你对于世界各国的首都了解得相当不错，但还有一些小地方需要加强哦！', null, null, 7,
        '2024-04-25 15:05:44', '2024-05-09 12:28:21', 0, 2, 1);

INSERT INTO scoring (id, result_name, result_detail, result_image_url, result_attributes, result_threshold, create_time,
                     update_time, deleted, app_id, user_id)
VALUES (19, '继续加油！', '还需努力哦', null, null, 0, '2024-04-25 15:05:44', '2024-05-09 12:28:21', 0, 2, 1);










