# -*- mode: ruby -*-
# vi: set ft=ruby :

ENV["LC_ALL"] = "en_US.UTF-8"

Vagrant.configure("2") do |config|
  config.vm.box = "centos/7"

  config.vm.provider "virtualbox" do |vb|
    vb.gui = false
    vb.memory = "2048"
    vb.cpus = 2
  end

  config.vm.provision "shell", inline: <<-SHELL
    yum install -y java-1.8.0-openjdk-devel.x86_64
  SHELL

  config.vm.define "springboot" do |srv|
    srv.vm.network "forwarded_port", guest: 8080, host: 8080
    srv.vm.provision :shell, inline: "cd /vagrant && nohup ./runSpringBoot.sh &", run: "always"
  end

  config.vm.define "netty" do |srv|
    srv.vm.network "forwarded_port", guest: 8081, host: 8081
    srv.vm.provision :shell, inline: "cd /vagrant && nohup ./runNetty.sh &", run: "always"
  end
end
