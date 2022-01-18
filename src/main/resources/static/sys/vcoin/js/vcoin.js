

/**
 * 提交保存
 */
function updateVCoin() {
    let vcoinForm = $("#vcoinForm").serializeObject();
    vcoinForm.updateTime = commonUtil.getNowTime();
    $.post(ctx + "/vcoin/incr", vcoinForm, function (data) {

        if(!data.flag){

            layer.msg(data.msg, {icon: 2,time: 2000}, function () {});
            return;
        }

        layer.msg("保存成功", {icon: 1, time: 2000}, function () {});

    });
}

