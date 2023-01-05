export function format(time: number) {
  const date = new Date(+time)

  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hour = date.getHours();
  const minute = date.getMinutes();
  const second = date.getSeconds();

  const monthText = month >= 10 ? month : '0' + month;

  const dayText = day >= 10 ? day : '0' + day;
  const hourText = hour >= 10 ? hour : '0' + hour;
  const minuteText = minute >= 10 ? minute : '0' + minute;
  const secondText = second >= 10 ? second : '0' + second;

  return date.getFullYear() + '-' + monthText + '-' + dayText + ' ' + hourText + ':' + minuteText
}