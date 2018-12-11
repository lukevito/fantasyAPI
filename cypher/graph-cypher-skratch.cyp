match ()-[a]-() delete a;
match (a) delete a;

call apoc.load.json("http://localhost:8080/all-beans") yield value
unwind value.contexts.application.beans as beans
with keys(beans) as beanNames, beans
foreach (beanName in beanNames |
  merge (b:Bean {name:beanName})
  ON CREATE SET b.scope = beans[beanName].scope,b.type = beans[beanName].type,b.resource = beans[beanName].resource
  ON MATCH SET  b.scope = beans[beanName].scope,b.type = beans[beanName].type,b.resource = beans[beanName].resource
  foreach (dependency in beans[beanName].dependencies |
    merge (depende:Bean {name:dependency})
    merge (b)-[d:DEPENDSON]->(depende)
  )
)
return count(*);

match p = ((b:Bean)-[]-()) return p limit 100;
match (b:Bean {name:"environment"}) return b;


call apoc.load.json("http://localhost:8080/players/seasons?year=2016") yield value
merge (p:Player {name:value.name}) on create set
p.blocks = value.blocks,
p.personal_fouls = value.personal_fouls,
p.attempted_field_goals = value.attempted_field_goals,
p.games_played = value.games_played,
p.steals = value.steals,
p.team = value.team,
p.made_free_throws = value.made_free_throws,
p.made_three_point_field_goals = value.made_three_point_field_goals,
p.assists = value.assists,
p.made_field_goals = value.made_field_goals,
p.minutes_played = value.minutes_played,
p.turnovers = value.turnovers,
p.attempted_free_throws = value.attempted_free_throws,
p.games_started = value.games_started,
p.position = value.position,
p.defensive_rebounds = value.defensive_rebounds,
p.offensive_rebounds = value.offensive_rebounds,
p.age = value.age,
p.attempted_three_point_field_goals = value.attempted_three_point_field_goals
merge (t:Team {name:value.team})
merge (p)-[:PLAYED]->(t);
