# reversi

ДЗ1 по КПО, Бирюлин Никита Андреевич, БПИ213.

Программа умеет:

* Разыгрывать матчи Игрок-против-Игрока (без флагов), Игрок-Против-Машины, Игрок-Против-Машины-Но-Круче (с просчетом ходов).
* Показывать лучшие результаты игрока за сессию.
* Есть возможность отменить любой ход, на любое количество ходов назад
* Все варианты хода выводятся на экран и визуализируется в виде точечек на игровом поле, как тут:

```
%	1	2	3	4	5	6	7	8
1	.	W	W	W	W	W	W	W	
2	B	E	.	B	W	W	W	W	
3	B	B	.	.	B	B	B	W	
4	B	B	B	W	W	W	B	W	
5	B	.	W	W	W	W	B	W	
6	B	B	W	W	B	W	B	W	
7	B	W	W	W	B	B	B	W	
8	W	E	W	W	W	W	.	W
```

* Таким образом, реализованы все требования.
* Посмотреть на пример игры в несколько раундов в рамках одной сессии можно по [ссылке](https://gist.github.com/keddad/bb75402df2636271afb2bf28b46b3677). (В этом примере я заставил машину
  играть с машиной, потому что столько играть в Реверси для примера я не готов)