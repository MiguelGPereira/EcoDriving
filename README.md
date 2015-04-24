# EcoDriving

==========
Instruções
==========

Prerequesitos:
PHP, MySql e MySQL Workbench instalados. Não estou a usar Apache, estou a usar o servidor integrado da versão 5 do php. É assumido são administradores, para usarem o utilizador "root" do mysql. Se têm um específico, alterem no início dos scripts. Têm que ter o servidor MySql a correr localmente(óbvio, I know). Eu estou a usar o MySql Workbench para explorar a base de dados. Vejam se têm um Schema criado de nome "test". Criem-no se não tiverem.
Procedimentos testados num sistema UNIX.

1. Abrir o MySQL Workbench.
2. Ir a File->Open SQL Script e abrir o "base.sql"
3. Executar o script completo(ícone do raio)
4. Navegar até ao diretório do projeto num terminal e correr o servidor php com o comando:  php -S localhost:8000
5. Abrir o browser em http://localhost:8000/ e confirmar se a query de exemplo funciona
6. Abrir o browser (uma vez)em http://localhost:8000/loadDatagrams.php para carregar as informações do onboardpc do comboio para a BD, para a tabela "Datagrams"


Para a componente Node:

1. Open command line on Node folder
2. npm install
3. node ./script.js
