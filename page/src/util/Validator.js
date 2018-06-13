export default new Object({
  userValidate: function(user) {
    var errorFields = []
    for (var prop in user) {
      if (user.hasOwnProperty(prop)) {
        if (
          !regexs.user.hasOwnProperty(prop) ||
          !String(user[prop]).match(regexs.user[prop])
        )
          errorFields.push(String(prop))
      }
    }
    return errorFields
  },
  examValidate: function(exam) {
    var errorFields = []
    for (var prop in regexs.exam) {
      if (exam.hasOwnProperty(prop)) {
        if (
          !regexs.exam.hasOwnProperty(prop) ||
          !String(exam[prop]).match(regexs.exam[prop])
        )
          errorFields.push(String(prop))
      }
    }
    var validBegEnd = function(user) {
      if (user.endNo < user.beginNo) return false

      if (user.beginNo <= 4) return endNo <= 4

      if (user.beginNo <= 8) return user.endNo >= 5 && user.endNo <= 8

      return user.endNo >= 9
    }
    if (errorFields.length === 0 && !validBegEnd(user)) {
      errorFields.push('beginNo')
      errorFields.push('endNo')
    }
    return errorFields
  }
})

var regexs = {
  user: {
    id: '^[0-9]{1,}$',
    teacherName: '^[\u2E80-\u9FFF]{2,5}$',
    teacherNo: '^[1-2][0-9]{9}$',
    loginPassword: '^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,}$',
    phone:
      '^((13[0-9])|(147,145)|(15(0,1,2,3,5,6,7,8,9))|(166)|(17[6-8])|(18[0-9])|(19[8-9]))[0-9]{8}$',
    manager: '^((true)|(false))$',
    male: '^((true)|(false))$',
    title: '^(?=\\s*\\S).*$',
    intro: '^(?=\\s*\\S).*$'
  },
  exam: {
    examDate: '^[1-9][0-9]{3}-[0-9]{2}-[0-9]{2}$',
    course: '^(?=\\s*\\S).*$',
    beginNo: '^(([1-9])|(1[0-2]))$',
    endNo: '^(([1-9])|(1[0-2]))$',
    area: '^((丹青)|(成栋)|(锦绣))楼$',
    classroomNo: '^([1-9]|(1[0-4]))((0[1-9])|(1[0-9])|(2[0-9])|3[0-6])$'
  }
}
