# SIMELA
Sistema Integrado para Monitorização da Esclerose Lateral Amiotrófica

Este trabalho enquadra-se nas áreas de aplicações médicas inserido na telemedicina, nomeadamente recolha, análise, visualização e processamento automático de informação. Em suma, permite monitorizar remotamente o estado clínico de um doente na sua própria casa e melhorar a continuidade dos cuidados e a sua eficiência. A monitorização num doente com Esclerose Lateral Amiotrófica (ELA ou ALS - Amiotrophic Lateral Sclerosis) torna-se uma componente fundamental para a observação, compreensão, conhecimento, previsão da evolução da doença e, atempadamente, agir de uma forma mais adequada e personalizada. A ELA é uma doença neurodegenerativa progressiva e rara, causada pela degeneração dos neurónios motores do cortéx cerebral que controlam os movimentos voluntários dos músculos reduzindo a qualidade de vida. A sobrevida média está descrita como sendo 3 a 5 anos.

Teve início, no ISEL durante um ano lectivo 2014-15 e resulta de uma colaboração com uma equipa médica do Hospital de Santa Maria (HSM) e do Instituto de Medicina Molecular (IMM) para a monitorização de doentes com ELA. Faz parte de um projeto multidisciplinar que envolve diferentes campos da engenharia, e está a ser desenvolvido no âmbito de três cursos ministrados pela ADEETC: MEETC, MEIC e LEIC.

1. ALS-Sense – Sistema de Aquisição e Processamento de Electromiogramas para Pacientes com ALS. Autor: Romil Kiritcumar Argi. (2014-15)
TFM em Engenharia de Electrónica e Telecomunicações. Orientadores: João Costa, Manuel Barata e Matilde Pós-de-Mina Pato
Um modelo de dados indicado para a recolha de registos da actividade eléctrica (electromiografia) depois de contracção voluntária, em ambulatório, em três zonas distintas dos pacientes. E, medidas de oximetria que mede indirectamente a quantidade de oxigénio no sangue (SpO2) de um doente. Este dispositivo tem capacidade para armazenar os sinais medidos e de os transmitir via Bluetooth.

2. ALS-Mon – Sistema para monitorizar ALS. Autor: Vasile Grigoras. (2014-15)
TFM em Engenharia Informática e de Computadores. Orientadores: Nuno Datia e Matilde Pós-de-Mina Pato
Um serviço e uma aplicação web que fazem a recepção dos dados de forma segura; um Data Warehouse (DW) onde os dados são verificados, processados e gravados autonomamente através do processo ETL automatizado que permite a extracção e a transformação destes dados e o carregamento deles no DW; duas APIs, OData e XMLA, que permitem a aplicação web cliente realizar interrogações sobre os dados recolhidos garantindo a segurança dos mesmos. A implementação do serviço utilizou ferramentas open-source.

3. ALS-Glance – Visualização de electromiografia com multiresolução temporal. Autor: Pedro Cardoso. (2015) 
TFC em Engenharia Informática e de Computadores. Orientadores: Nuno Datia e Matilde Pós-de-Mina Pato
Uma aplicação web cliente que permite ao utilizador, após autenticação, visualizar a ficha de cada paciente e selecionar filtros interativos visualizando os resultados instantaneamente. A web api é responsável por fornecer a informação do modelo numa forma agnóstica, não se comprometendo com o sistema de gestão de base de dados nem com os consumidores dessa informação. É acessível para dispositivos móveis, computadores portáteis ou de secretária. A implementação utilizou ferramentas open-source.

4. ALSrm App - Visualização de Biosinais. Autores: Tiago Bogalho e Nuno Malés. (2016) 
TFC em Engenharia Informática e de Computadores. Orientadores: Nuno Datia e Matilde Pós-de-Mina Pato
O ALSrm é uma plataforma que pretende dar apoio aos doentes de ELA através da monitorização contínua dos sinais vitais, níveis de actividade física e modificações respiratórias. O ALSrm é constituído por um dispositivo, BITalino, que permite monitorizar as várias medidas e processa essa informação para um conjunto de serviços, nomeadamente (i) gestão e visualização da informação básica do utilizador; (ii) visualização dos valores medidos; (iii) sistema de gestão de alerta. O software utilizado é Open Signals que consiste em visualizar gráficos e recolher informação dos sensores, via bluetooth 4.0. O armazenamento de dados está realizado através de Postgresql e a interacção cliente/servidor está realizada com recurso à norma OData.

MPato, Dezembro 2016
