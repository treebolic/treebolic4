<?xml version="1.0" encoding="UTF-8"?>

<!--
  ~ Copyright (c) 2022. Bernard Bou
  -->

<!-- TREEBOLIC 2 DOT - 13/09/2014 (C) 2008-2014 Author: Bernard Bou -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" indent="no" encoding="UTF8" omit-xml-declaration="yes" />

	<xsl:template match="/">
		<xsl:text>digraph G {&#xA;/* converted from treebolic */&#xA;</xsl:text>
		<xsl:apply-templates select="./treebolic" />
		<xsl:text>}&#xA;</xsl:text>
	</xsl:template>

	<xsl:template match="treebolic">
		<xsl:apply-templates select="./tree" />
	</xsl:template>

	<xsl:template match="tree">
		<!-- TREEBOLIC -->
		<xsl:text>graph [</xsl:text>
		<xsl:text>&#xA;</xsl:text>
		<xsl:apply-templates select="//treebolic/@*">
			<xsl:with-param name="context">
				treebolic
			</xsl:with-param>
		</xsl:apply-templates>
		<!-- TREE -->
		<xsl:text>&#xA;</xsl:text>
		<xsl:apply-templates select="./@*">
			<xsl:with-param name="context">
				tree
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:text>&#xA;</xsl:text>
		<xsl:apply-templates select="./img/@*">
			<xsl:with-param name="context">
				tree_img
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:text>&#xA;];&#xA;</xsl:text>
		<!-- NODES -->
		<xsl:text>node [</xsl:text>
		<xsl:text>&#xA;</xsl:text>
		<xsl:apply-templates select="./nodes/@*">
			<xsl:with-param name="context">
				nodes
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:text>&#xA;</xsl:text>
		<xsl:apply-templates select="./nodes/img/@*">
			<xsl:with-param name="context">
				nodes_img
			</xsl:with-param>
		</xsl:apply-templates>
		<!-- default.treeedge -->
		<xsl:text>&#xA;</xsl:text>
		<xsl:apply-templates select="./nodes/default.treeedge/@*">
			<xsl:with-param name="context">
				nodes_default_treeedge
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:text>&#xA;</xsl:text>
		<xsl:apply-templates select="./nodes/default.treeedge/img/@*">
			<xsl:with-param name="context">
				nodes_default_treeedge_img
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:text>&#xA;];&#xA;</xsl:text>
		<xsl:text>edge [</xsl:text>
		<!-- EDGES -->
		<xsl:text>&#xA;</xsl:text>
		<xsl:apply-templates select="./edges/@*">
			<xsl:with-param name="context">
				edges
			</xsl:with-param>
		</xsl:apply-templates>
		<!-- default.edges -->
		<xsl:text>&#xA;</xsl:text>
		<xsl:apply-templates select="./edges/default.edge/@*">
			<xsl:with-param name="context">
				edges_default_edge
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="./edges/default.edge/img/@*">
			<xsl:with-param name="context">
				edges_default_edge_img
			</xsl:with-param>
		</xsl:apply-templates>
		<!-- end -->
		<xsl:text>&#xA;]&#xA;</xsl:text>
		<xsl:text>/* T R E E */&#xA;</xsl:text>
		<xsl:apply-templates select=".//node" />
		<xsl:text>/* N O N T R E E */&#xA;</xsl:text>
		<xsl:apply-templates select=".//edge" />
	</xsl:template>

	<xsl:template match="node">
		<!-- node -->
		<xsl:text>	</xsl:text>
		<xsl:value-of select="translate(@id,'-','_')" />
		<xsl:text> [</xsl:text>
		<xsl:apply-templates select="./label" />
		<xsl:apply-templates select="./content" />
		<xsl:apply-templates select="./a" />
		<xsl:apply-templates select="./img" />
		<xsl:apply-templates select="./mountpoint" />
		<xsl:apply-templates select="./@*" />
		<xsl:text> ];&#xA;</xsl:text>
		<!-- treeedge -->
		<xsl:if test="../@id!=''">
			<xsl:text>	</xsl:text>
			<xsl:value-of select="translate(../@id,'-','_')" />
			<xsl:text disable-output-escaping="yes"> -> </xsl:text>
			<xsl:value-of select="translate(./@id,'-','_')" />
			<xsl:text> [</xsl:text>
			<xsl:text> type="tree"</xsl:text>
			<xsl:apply-templates select="./treeedge/label" />
			<xsl:apply-templates select="./treeedge/img" />
			<xsl:apply-templates select="./treeedge/@*" />
			<xsl:text> ];&#xA;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="edge">
		<xsl:text>	</xsl:text>
		<xsl:value-of select="translate(./@from,'-','_')" />
		<xsl:text disable-output-escaping="yes"> -> </xsl:text>
		<xsl:value-of select="translate(./@to,'-','_')" />
		<xsl:text> [</xsl:text>
		<xsl:text> type="nontree"</xsl:text>
		<xsl:apply-templates select="./label" />
		<xsl:apply-templates select="./img" />
		<xsl:apply-templates select="./@*" />
		<xsl:text> ];&#xA;</xsl:text>
	</xsl:template>

	<xsl:template match="label">
		<xsl:param name="context"/>
		<xsl:variable name="attrname">
			<xsl:choose>
				<xsl:when test="$context != ''">
					<xsl:value-of select="concat($context,'_','label')" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>label</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:text> </xsl:text>
		<xsl:value-of select="$attrname" />
		<xsl:text>="</xsl:text>
		<xsl:apply-templates select="text()" />
		<xsl:text>"</xsl:text>
	</xsl:template>

	<xsl:template match="content">
		<xsl:text> content="</xsl:text>
		<xsl:apply-templates select="text()" />
		<xsl:text>"</xsl:text>
	</xsl:template>

	<xsl:template match="a">
		<xsl:apply-templates select="@href">
			<xsl:with-param name="context">
				link
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="img">
		<xsl:param name="context"/>
		<xsl:variable name="attrname">
			<xsl:choose>
				<xsl:when test="$context != ''">
					<xsl:value-of select="concat($context,'_','img')" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>img</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:apply-templates select="@src">
			<xsl:with-param name="context">
				<xsl:value-of select="$attrname" />
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="mountpoint">
		<xsl:apply-templates select="./a/@href">
			<xsl:with-param name="context">
				mountpoint
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of disable-output-escaping="yes" select="normalize-space(.)" />
	</xsl:template>

	<xsl:template match="@*">
		<xsl:param name="context"/>
		<xsl:variable name="attrname">
			<xsl:choose>
				<xsl:when test="$context != ''">
					<xsl:value-of select="concat($context,'_',name())" />
				</xsl:when>
				<xsl:when test="name() = 'color'">
					<xsl:value-of select="concat('edge_',name())" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="name()" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- <xsl:if test="$context != ''"> <xsl:message>context=<xsl:value-of select="$context"/></xsl:message> </xsl:if> -->
		<xsl:if test="name()!='id'">
			<xsl:text/>
			<xsl:value-of select="concat(' ',translate($attrname,'-','_'))" />
			<xsl:text>="</xsl:text>
			<xsl:choose>
				<xsl:when test="contains(name(),'color')">
					<xsl:value-of select="concat('#',.)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="." />
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>"</xsl:text>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
