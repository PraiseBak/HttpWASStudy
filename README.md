http 통신을 지원하는 WAS를 구현해본 프로젝트입니다.

- 소켓 통신으로 각 Request마다 Thread 생성
- @RequestBody,@RequestParam등 스프링부트에서 자주 사용되는 어노테이션을 변형한 MethodMapping,RequestBody를 자바 리플렉션을 이용하여 만들어 보았습니다. 