
VERSION=2.0-beta-12
JAVADOC=javadoc
JAVAC=javac
JAVA=java
JAR=jar
MODULES="core smf io base java python groovy jsr94"

export JAVA JAVAC JAR
##
##
##

build()
{
  local target;

  for target in $* ; do
    done_var=done_$target
    done=${!done_var}
    if [ "${done}" != "done" ] ; then
      export ${done_var}="done"
      target_${target}
    fi
  done 
}

target_all()
{
  build compile javadoc site
}

target_site()
{
  #build javadoc

  for module in $MODULES ; do
    module_site $module
  done

  copy_tree $BASE/build/docs/api $BASE/build/site/api 
}

module_site()
{
  local module=$1

  if [ ! -d drools-$module/site ] ; then
    return
  fi
  
  for path in $(cd $BASE/site; find . -name '*.html') ; do
    generate_root_page $path 
  done

  for path in $(cd drools-$module/site; find . -name '*.html') ; do
    generate_module_page $path $module
  done
}

generate_root_page()
{
  local path=$1
  local page=$(basename $path)
  local out=$BASE/build/site/$page

  echo "page $page"

  mkdir -p $(dirname $out)

  cat $BASE/lib/site/first.html > $out
  generate_root_nav $page $out
  cat $BASE/lib/site/middle.html >> $out
  while read line ; do
    echo "$line" >> $out
  done < ./site/$page
  cat $BASE/lib/site/last.html >> $out
}

generate_module_page()
{
  local path=$1
  local module=$2
  local page=$(basename $path)
  local out=$BASE/build/site/$module/$page

  echo "module $module"
  echo "page $page"

  mkdir -p $(dirname $out)

  cat $BASE/lib/site/first.html > $out
  generate_module_nav $page $module $out
  cat $BASE/lib/site/middle.html >> $out
  while read line ; do
    echo "$line" >> $out
  done < ./drools-$module/site/$page
  cat $BASE/lib/site/last.html >> $out
}

generate_root_nav()
{
  local page=$1
  local out=$2

  generate_local_nav $page $out ./site/nav

  local module

  for module in $MODULES ; do 
    if [ -f drools-$module/site/nav ] ; then
      generate_nonlocal_nav "$out" "drools-$module/site/nav" "$module" ""
    fi
  done
  
}

generate_module_nav()
{
  local page=$1
  local thismodule=$2
  local out=$3

  local module

  generate_nonlocal_nav $out $BASE/site/nav ".." ""

  for module in $MODULES ; do 
    if [ -f drools-$module/site/nav ] ; then
      if [ "$module" == "$thismodule" ] ; then 
        generate_local_nav "$page" "$out" "drools-$module/site/nav"
      else
        generate_nonlocal_nav "$out" "drools-$module/site/nav" "$module" "../"
      fi
    fi
  done
}

generate_local_nav()
{
  local page=$1
  local out=$2
  local nav=$3

  local first="first"

  echo '<div class="navSection">' >> $out

  while read line ; do
    echo "$line" | grep '^=' 2>&1 > /dev/null
    if [ $? -eq 0 ] ; then
      if [ $first == "first" ] ; then
        first=notfirst
      else
        echo "</div>" >> $out
        echo '<div class="navSection">' >> $out
      fi
      local header=$(echo $line | cut -f 2 -d =)
      echo "  <div class=\"navSectionHead\">$header</div>" >> $out
    else
      local url=$(echo $line | cut -f 1 -d \|)
      local desc=$(echo $line | cut -f 2 -d \|)
      if [ "$url" == "$page" ] ; then
        echo "    <div class=\"navLink\"><small><a href=\"$url\" style=\"font-weight: bold\">$desc</a></small></div>" >> $out
      else
        echo "    <div class=\"navLink\"><small><a href=\"$url\">$desc</a></small></div>" >> $out
      fi
    fi
  done < "$nav"

  echo '</div>' >> $out
}

generate_nonlocal_nav()
{
  local out=$1
  local nav=$2
  local navmodule=$3
  local prefix=$4

  local first="first"

  echo '<div class="navSection">' >> $out

  while read line ; do
    echo "$line" | grep '^=' 2>&1 > /dev/null
    if [ $? -eq 0 ] ; then
      if [ $first == "first" ] ; then
        first=notfirst
      else
        echo "</div>" >> $out
        echo '<div class="navSection">' >> $out
      fi
      local header=$(echo $line | cut -f 2 -d =)
      echo "  <div class=\"navSectionHead\">$header</div>" >> $out
    else
      local url=$(echo $line | cut -f 1 -d \|)
      local desc=$(echo $line | cut -f 2 -d \|)
      echo "    <div class=\"navLink\"><small><a href=\"$prefix$navmodule/$url\">$desc</a></small></div>" >> $out
    fi
  done < "$nav"

  echo '</div>' >> $out
}

target_jsr94()
{
  build core smf io 
  target_compile jsr94
}

target_groovy()
{
  build core smf
  target_compile groovy
}

target_python()
{
  build core smf
  target_compile python
}

target_java()
{
  build core smf
  target_compile java
}

target_base()
{
  build core smf
  target_compile base
}

target_io()
{
  build core smf
  target_compile io
}

target_smf()
{
  build core
  target_compile smf
}

target_core()
{
  target_compile core
}

target_prepare()
{
  echo "preparing filesystem"
  mkdir -p $BASE/build/
}

target_compile()
{
  local modules=$1

  if [ -z $modules ] ; then
    modules=$MODULES
  fi
  build prepare

  local module 

  for module in $modules ; do 
    module_build     $module
    module_compile   $module
    module_make_jar  $module
    module_copy_jar  $module
    module_copy_deps $module
  done
}

module_build()
{
  if [ ! -f drools-$module/build.sh ] ; then
    return
  fi

  cd drools-$module
  /bin/sh build.sh  
  cd -
}

module_compile()
{
  local module=$1

  mkdir -p drools-$module/build/classes/
  echo "compiling module $module"
  copy_tree drools-$module/src/main/ drools-$module/build/sources/ java 
  _javac $module
}

module_make_jar()
{
  local module=$1

  echo "jarring module $module"
  copy_tree drools-$module/src/main/ drools-$module/build/classes/ properties 
  copy_tree drools-$module/src/conf/ drools-$module/build/classes/ '*'
  _jar $module
}

module_copy_jar()
{
  local module=$1

  echo "copying $module jar"
  mkdir -p $BASE/build/lib/

  cp drools-$module/build/drools-$module-$VERSION.jar $BASE/build/lib
}

module_copy_deps()
{
  local module=$1

  echo "copy $module dependencies"
  mkdir -p $BASE/build/lib/
  if [ -d drools-$module/lib ] ; then
    cp drools-$module/lib/*.jar $BASE/build/lib
  fi
}

target_javadoc()
{
  build compile

  echo "building javadocs"

  local sourcepath=""

  for module in $MODULES ; do 
    if [ -z $sourcepath ] ; then
      sourcepath=./drools-$module/build/sources/
    else
      sourcepath=$sourcepath:./drools-$module/build/sources/
    fi
  done

  $JAVADOC \
      -classpath $(dyn_javadoc_classpath) \
      -sourcepath $sourcepath \
      -windowtitle "Drools $VERSION Public API" \
      -use \
      -version \
      -author \
      -d $BASE/build/docs/api/ -subpackages org.drools:bsh.commands\
      -group "Core Engine" org.drools:org.drools.rule:org.drools.conflict \
      -group "Semanic Providers Interface" org.drools.spi \
      -group "Semantic Module Framework" org.drools.smf \
      -group "Rule I/O" org.drools.io \
      -group "Base Semantic Module" org.drools.semantics.base \
      -group "Java Semantic Module" org.drools.semantics.java:org.drools.semantics.java.parser:bsh.commands \
      -group "Python Semantic Module" org.drools.semantics.python \
      -group "Groovy Semantic Module" org.drools.semantics.groovy \
      -group "JSR-94 Binding" org.drools.jsr94:org.drools.jsr94.rules:org.drools.jsr94.rules.admin:org.drools.jsr94.jca.spi \
      -exclude org.drools.reteoo \
      org.drools \
      bsh.commands 
}

target_clean()
{
  echo "cleaning filesystem"
  rm -Rf $BASE/build

  local module 

  for module in $MODULES ; do 
    rm -Rf drools-$module/build 
  done
}

_javac()
{
  local module=$1

  cd drools-$module

  $JAVAC \
    -classpath $(dyn_classpath) \
    -sourcepath ./build/sources/ \
    -d ./build/classes/ \
    -deprecation \
    -g \
    $(find ./build/sources/ -name '*.java')

  cd -
}

_jar()
{
  local module=$1

  cd drools-$module

  $JAR -cf ./build/drools-$module-$VERSION.jar -C ./build/classes . 

  cd - 
}

copy_tree()
{
  local source=$1
  local dest=$2
  local exts="$3 $4 $5 $6 $7 $8 $9"

  if [ ! -d $source ] ; then
    return
  fi

  mkdir -p $dest
  cd $dest
  dest=$PWD
  cd -

  cd $source

  if [ -z $exts ] ; then
    find ./ -depth -print | cpio -pudm --quiet $dest
  else
    for ext in $exts ; do
      find ./ -depth \( -name *.$ext \) -print | cpio -pudm --quiet $dest
    done
  fi

  cd -
}

function dyn_javadoc_classpath()
{
  local jars=$BASE/build/lib/*.jar

  local cp=""

  for jar in $jars ; do
    if [ -z $cp ] ; then
      cp=$jar
    else
      cp="$cp:$jar"
    fi
  done

  echo $cp
}

dyn_classpath()
{
  local common=$(dyn_common_classpath)
  local lib=$(dyn_lib_classpath)

  local cp=""

  if [ ! -z $common ] ; then
    cp=$common
  fi

  if [ ! -z $lib ] ; then
    if [ -z $cp ] ; then
      cp=$lib
    else
      cp=$cp:$lib
    fi
  fi 

  echo $cp 
}

function dyn_lib_classpath()
{
  if [ ! -d ./lib/ ] ; then
    echo ""
    return
  fi;

  local jars="lib/*.jar"

  local cp=""

  for jar in $jars ; do
    if [ -z $cp ] ; then
      cp=$jar
    else
      cp="$cp:$jar"
    fi
  done

  echo $cp
}

dyn_common_classpath()
{
  local jars=$BASE/build/lib/*.jar

  local cp=""

  for jar in $jars ; do
    if [ -z $cp ] ; then
      cp=$jar
    else
      cp="$cp:$jar"
    fi
  done

  echo $cp
}


##
##
##

default_targets="all"

if [ -z "$*" ] ; then
  targets=$default_targets
else
  targets=$*
fi

BASE=$PWD

build $targets
