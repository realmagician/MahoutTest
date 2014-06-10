MahoutTest
==========

MahoutTest
基本信息
==========

测试Mahout推荐效果，目前采用非分布式。测试数据采用grouplens提供的Movielen。
由于Mahout不支持content based，所以只用Mahout提供的similarity功能计算电影之间的相似度。再从以相似度最高的5个作为推荐结果

TODO
==========

多层标签计算，item-user的similarity计算，与cf结合的推荐
