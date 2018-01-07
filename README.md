실행방법!

markdown으로 Blogger 포스팅 하는 어플리케이션

! ${} 각 바인딩 부분을 채워주면 완성

> ./gradlew run -Pfilename="posts/2018-01-01-test.md" -PblogUrl="${blogger url}" -PclientId="${google api client id}" -PclientSecret="${google api client secret}"